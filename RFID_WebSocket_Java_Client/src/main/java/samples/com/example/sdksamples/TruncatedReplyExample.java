package examples;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.Settings;
import com.impinj.octane.TruncatedReplySettings;

import java.util.Scanner;


public class TruncatedReplyExample {
    public static void main(String[] args) {
        try {
            String hostname = "impinj-1d-00-28.i4j.io";

            if (hostname == null) {
                throw new Exception("Must specify the '"
                        + SampleProperties.hostname + "' property");
            }

            ImpinjReader reader = new ImpinjReader();

            // Connect
            System.out.println("Connecting to " + hostname);
            reader.connect(hostname);
            
            // Get the default settings
            Settings settings = reader.queryDefaultSettings();

            TruncatedReplySettings trs = settings.getTruncatedReply();

            trs.setIsEnabled(true);
            trs.setGen2v2TagsOnly(false);
            trs.setEpcLengthInWords((byte) 6);
            trs.setBitPointer((short) 32);
            trs.setTagMask("300833B2DDD90140");

            // Apply the new settings
            reader.applySettings(settings);

            // connect a listener
            reader.setTagReportListener(new TagReportListenerImplementation());

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
    }
}