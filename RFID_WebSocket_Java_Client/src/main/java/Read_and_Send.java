import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.websocket.Session;
import com.impinj.octane.*;

public class Read_and_Send {
    static Queue<String> queue = new LinkedList<String>();
    static Boolean done = false;
    static int sum = 0;
    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        //创建一个与Python服务器的ws连接
        WebSocketClient client = new WebSocketClient(new URI("ws://localhost:8888"));
        Session session = client.getSession();
//            try (BufferedReader br = Files.newBufferedReader(Paths.get("D:\\RFID实验数据\\20210515\\6tags_1human.csv"))) {
//                // CSV文件的分隔符
//                String DELIMITER = ",";
//                // 按行读取
//                String line;
//                while ((line = br.readLine()) != null) {
//                    // 分割
//                    String[] columns = line.split(DELIMITER);
//                    // 打印行
//                    session.getBasicRemote().sendText(String.join(", ", columns));
//                }
//                done = true;
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
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
                        String s = "";
                        System.out.print(" EPC: " + t.getEpc().toString());
                        s+=" EPC: " + t.getEpc().toString();

                        if (reader.getName() != null) {
                            System.out.print(" Reader_name: " + reader.getName());
                            s+=" Reader_name: " + reader.getName();
                        } else {
                            System.out.print(" Reader_ip: " + reader.getAddress());
                            s+=" Reader_ip: " + reader.getAddress();
                        }

                        if (t.isAntennaPortNumberPresent()) {
                            System.out.print(" antenna: " + t.getAntennaPortNumber());
                            s+=" antenna: " + t.getAntennaPortNumber();
                        }

                        if (t.isFirstSeenTimePresent()) {
                            System.out.print(" first: " + t.getFirstSeenTime().ToString());
                            s+=" first: " + t.getFirstSeenTime().ToString();
                        }

                        if (t.isLastSeenTimePresent()) {
                            System.out.print(" last: " + t.getLastSeenTime().ToString());
                            s+=" last: " + t.getLastSeenTime().ToString();
                        }

                        if (t.isSeenCountPresent()) {
                            System.out.print(" count: " + t.getTagSeenCount());
                            s+=" count: " + t.getTagSeenCount();
                        }

                        if (t.isRfDopplerFrequencyPresent()) {
                            System.out.print(" doppler: " + t.getRfDopplerFrequency());
                            s+=" doppler: " + t.getRfDopplerFrequency();
                        }

                        if (t.isPeakRssiInDbmPresent()) {
                            System.out.print(" peak_rssi: " + t.getPeakRssiInDbm());
                            s+=" peak_rssi: " + t.getPeakRssiInDbm();
                        }

                        if (t.isChannelInMhzPresent()) {
                            System.out.print(" chan_MHz: " + t.getChannelInMhz());
                            s+=" chan_MHz: " + t.getChannelInMhz();
                        }

                        if (t.isRfPhaseAnglePresent()) {
                            System.out.print(" phase angle: " + t.getPhaseAngleInRadians());
                            s+=" phase angle: " + t.getPhaseAngleInRadians();
                        }

                        if (t.isFastIdPresent()) {
                            System.out.print("\n     fast_id: " + t.getTid().toHexString());
                            s+="\n     fast_id: " + t.getTid().toHexString();

                            System.out.print(" model: " +
                                    t.getModelDetails().getModelName());
                            s+=" model: " +
                                    t.getModelDetails().getModelName();

                            System.out.print(" epcsize: " +
                                    t.getModelDetails().getEpcSizeBits());
                            s+=" epcsize: " +
                                    t.getModelDetails().getEpcSizeBits();

                            System.out.print(" usermemsize: " +
                                    t.getModelDetails().getUserMemorySizeBits());
                            s+=" usermemsize: " +
                                    t.getModelDetails().getUserMemorySizeBits();
                        }
                        try {
                            sum += 1;
                            System.out.println(s);
                            session.getBasicRemote().sendText(s);
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
