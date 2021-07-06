package socket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.websocket.Session;
import com.impinj.octane.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Read_and_Send {
    static Queue<String> queue = new LinkedList<String>();
    static Boolean done = false;
    static int sum = 0;
    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        //创建一个与Python服务器的ws连接
        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:8001"));
        final Session session = client.getSession();
        try {
            String hostname = "192.168.1.27";

            if (hostname == null) {
                throw new Exception("Must specify the '"
                        + SampleProperties.hostname + "' property");
            }

            ImpinjReader reader = new ImpinjReader(hostname,"1",10000);

            // Connect
            reader.connect(hostname);
            System.out.println("Connecting to " + hostname);

            // Get the default settings
            Settings settings = reader.queryDefaultSettings();
            FeatureSet featureSet = reader.queryFeatureSet();

            // Apply the new settings

            AntennaConfigGroup ac = settings.getAntennas();
            ac.enableAll();

            ReportConfig reportConfig = settings.getReport();
            reportConfig.setIncludeAntennaPortNumber(true);
            reportConfig.setIncludePhaseAngle(true);
            reportConfig.setIncludeFastId(false);

            settings.setSession(0);
            settings.setTagPopulationEstimate(32);
            settings.setRfMode(0);
            settings.setSearchMode(SearchMode.DualTarget);

            reader.applySettings(settings);

            // connect a listener
            reader.setTagReportListener(new TagReportListenerImplementation(){
                @Override
                public void onTagReported(ImpinjReader reader, TagReport report) {
                    List<Tag> tags = report.getTags();

                    for (Tag t : tags) {
                        JsonObject msgObj = new JsonObject();
                        System.out.print(" EPC: " + t.getEpc().toString());
                        msgObj.addProperty("EPC",t.getEpc().toString());

                        if (reader.getName() != null) {
                            System.out.print(" Reader_name: " + reader.getName());
                            msgObj.addProperty("Reader_name",reader.getName());
                        } else {
                            System.out.print(" Reader_ip: " + reader.getAddress());
                            msgObj.addProperty("Reader_ip",reader.getAddress());
                        }

                        if (t.isAntennaPortNumberPresent()) {
                            System.out.print(" antenna: " + t.getAntennaPortNumber());
                            msgObj.addProperty("antenna",t.getAntennaPortNumber());
                        }

                        if (t.isFirstSeenTimePresent()) {
                            System.out.print(" first: " + t.getFirstSeenTime().ToString());
                            msgObj.addProperty("first",t.getFirstSeenTime().ToString());
                        }

                        if (t.isLastSeenTimePresent()) {
                            System.out.print(" last: " + t.getLastSeenTime().ToString());
                            msgObj.addProperty("last",t.getLastSeenTime().ToString());
                        }

                        if (t.isSeenCountPresent()) {
                            System.out.print(" count: " + t.getTagSeenCount());
                            msgObj.addProperty("count",t.getTagSeenCount());
                        }

                        if (t.isRfDopplerFrequencyPresent()) {
                            System.out.print(" doppler: " + t.getRfDopplerFrequency());
                            msgObj.addProperty("doppler",t.getRfDopplerFrequency());
                        }

                        if (t.isPeakRssiInDbmPresent()) {
                            System.out.print(" peak_rssi: " + t.getPeakRssiInDbm());
                            msgObj.addProperty("peak_rssi",t.getPeakRssiInDbm());
                        }

                        if (t.isChannelInMhzPresent()) {
                            System.out.print(" chan_MHz: " + t.getChannelInMhz());
                            msgObj.addProperty("channel",t.getChannelInMhz());
                        }

                        if (t.isRfPhaseAnglePresent()) {
                            System.out.print(" phase angle: " + t.getPhaseAngleInRadians());
                            msgObj.addProperty("phase_angle",t.getPhaseAngleInRadians());
                        }

                        try {
                            sum += 1;
                            System.out.println(msgObj.toString());
                            session.getBasicRemote().sendText(msgObj.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            // Start the reader
            reader.start();

            System.out.println("Press Enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            System.out.println("Stopping  " + hostname);
            reader.stop();
            session.close();

            System.out.println("Disconnecting from " + hostname);
            reader.disconnect();

            System.out.println("Done");
        } catch (OctaneSdkException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace(System.out);
        }
        System.out.println("sum:"+sum);

    }
}
