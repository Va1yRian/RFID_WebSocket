package examples;

import com.impinj.octane.*;

import java.util.Scanner;

/**
 * This example shows how to use the tag select filter interface, which allows
 * for more control of the select commands performed by the reader to filter
 * tags.
 *
 * Using this interface allows for up to 5 select commands to be sent with
 * latest versions of octane firmware.
 */
public class ReadTagsUseTagSelectFilter {

    public static void main(String[] args) {
        try {
            String hostname = System.getProperty(SampleProperties.hostname);

            if (hostname == null) {
                throw new Exception("Must specify the '"
                        + SampleProperties.hostname + "' property");
            }

            ImpinjReader reader = new ImpinjReader();
            System.out.println("Connecting to " + hostname);
            reader.connect(hostname);

            Settings settings = reader.queryDefaultSettings();
            settings.getReport().setIncludeAntennaPortNumber(true);
            settings.getReport().setMode(ReportMode.Individual);
            settings.setRfMode(1002);

            String targetEpc = System.getProperty(SampleProperties.targetTag);

            // Enable tag select filter interface
            settings.getFilters().setMode(TagFilterMode.UseTagSelectFilters);

            // Setup filters
            // The set of filters we are adding should inventory any tags
            // which have an EPC which starts with a "3", and ends with
            // a "A", "B", "C" or "D".
            TagSelectFilter filter1 = new TagSelectFilter();

            filter1.setMatchingAction(TagFilterStateUnawareAction.Select);
            filter1.setNonMatchingAction(TagFilterStateUnawareAction.DoNothing);
            filter1.setTagMask("A");
            filter1.setBitPointer(BitPointers.Epc + 80);  // start matching the last 16 bits of EPC
            filter1.setMemoryBank(MemoryBank.Epc);

            TagSelectFilter filter2 = new TagSelectFilter();

            filter2.setMatchingAction(TagFilterStateUnawareAction.Select);
            filter2.setNonMatchingAction(TagFilterStateUnawareAction.DoNothing);
            filter2.setTagMask("B");
            filter2.setBitPointer(BitPointers.Epc + 84);
            filter2.setMemoryBank(MemoryBank.Epc);

            TagSelectFilter filter3 = new TagSelectFilter();

            filter3.setMatchingAction(TagFilterStateUnawareAction.Select);
            filter3.setNonMatchingAction(TagFilterStateUnawareAction.DoNothing);
            filter3.setTagMask("C");
            filter3.setBitPointer(BitPointers.Epc + 88);
            filter3.setMemoryBank(MemoryBank.Epc);

            TagSelectFilter filter4 = new TagSelectFilter();

            filter4.setMatchingAction(TagFilterStateUnawareAction.Select);
            filter4.setNonMatchingAction(TagFilterStateUnawareAction.DoNothing);
            filter4.setTagMask("D");
            filter4.setBitPointer(BitPointers.Epc + 92);
            filter4.setMemoryBank(MemoryBank.Epc);

            TagSelectFilter filter5 = new TagSelectFilter();

            filter5.setMatchingAction(TagFilterStateUnawareAction.DoNothing);
            filter5.setNonMatchingAction(TagFilterStateUnawareAction.Unselect);
            filter5.setTagMask("3");
            filter5.setBitPointer(BitPointers.Epc + 0);  // match the fist 4 bits of EPC
            filter5.setMemoryBank(MemoryBank.Epc);

            // Add filters
            settings.getFilters().getTagSelectFilterList().add(filter1);
            settings.getFilters().getTagSelectFilterList().add(filter2);
            settings.getFilters().getTagSelectFilterList().add(filter3);
            settings.getFilters().getTagSelectFilterList().add(filter4);
            settings.getFilters().getTagSelectFilterList().add(filter5);

            reader.applySettings(settings);
            reader.setTagReportListener(new TagReportListenerImplementation());
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
