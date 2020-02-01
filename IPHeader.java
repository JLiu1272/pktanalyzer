import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class IPHeader implements Constants {

    // The type of header we are printing
    private static String type = "IP: ";

    // Keep track of the index where we read up to
    // This need to be a static variable and global to the
    // class as it
    private static int startByte = 0;

    // A flag for determining whether options exist
    // or not. By default, there are no options
    private static boolean hasOptions = false;

    // Protocol so that we can print the right set of data
    private static int PROTOCOL = 0;

    // A hashmap containing the header keys and
    // their byte length
    LinkedHashMap<String, Integer[]> dataTypes = new LinkedHashMap<>();

    // Header Length
    private static int headerLength = 0;

    public IPHeader(int cursor) {

        startByte = cursor;

        // Init the dataTypes hash values with the dataTypes
        // that will exist
        // The the value is a bit representation. Hence, 1 = 1 bit

        // Array field representation
        // Field 1 - The Bit Length
        // Field 2 - The Type of data we want to convert to
        // Field 3 - The unit type
        // --------------------------------------------------
        // Field 2 Options
        // * 1 = Convert to decimal value
        // * 2 = Convert to hex value
        // * 3 = Obtain IP Address
        // * 4 = Print the options flag
        dataTypes.put("Version", new Integer[] { 4, 1, 99 });
        dataTypes.put("Header Length", new Integer[] { 4, 1, 1 });
        dataTypes.put("(DSCP)", new Integer[] { 8, 2, 99 });
        dataTypes.put("Total length", new Integer[] { 16, 1, 1 });
        dataTypes.put("Identification", new Integer[] { 16, 1, 99 });
        dataTypes.put("Flags", new Integer[] { 8, 2, 99 });
        dataTypes.put("Fragment offset", new Integer[] { 8, 1, 1 });
        dataTypes.put("Time to live", new Integer[] { 8, 1, 2 });
        dataTypes.put("Protocol", new Integer[] { 8, 1, 99 });
        dataTypes.put("Header checksum", new Integer[] { 16, 2, 99 });
        dataTypes.put("Source address", new Integer[] { 32, 3, 99 });
        dataTypes.put("Destination address", new Integer[] { 32, 3, 99 });
        dataTypes.put("Options", new Integer[] { 0, 4, 99 });
    }

    public void printIPHeader(Byte[] data) {

        // Obtaining the data that are for IP Header
        Byte[] ipData = Arrays.copyOfRange(data, startByte, data.length);

        System.out.println(type + "----- " + type + "Header  -----");
        System.out.println(type);

        Util util = new Util();

        String binary = util.byteArrToBinary(ipData);

        // This is to keep track of where we left off from the last bit
        int startBit = 0;

        for (String title : dataTypes.keySet()) {
            int bitLen = dataTypes.get(title)[0];
            int processType = dataTypes.get(title)[1];
            int unitNum = dataTypes.get(title)[2];
            String binaryChunk = binary.substring(startBit, startBit + bitLen);

            if (title != "Flags") {
                printHeaderContent(title, processType, unitNum, binaryChunk);
            }

            if (title == "(DSCP)") {
                printDSCP(title, binaryChunk, bitLen);
            } else if (title == "Flags") {
                String binaryFlagChunk = binary.substring(startBit, startBit + 16);
                int decimal = Integer.parseInt(binaryFlagChunk, 2);
                String hex = "0x" + Integer.toHexString(decimal);

                // Print the values
                String formatted = String.format(type + "%-25s = %20s", title, hex);
                System.out.println(formatted);
                printFlagsDetails(title, binaryChunk);
            }

            // Advance the cursor for keeping track of the bit
            startBit += bitLen;
        }

        System.out.println(type);
    }

    /**
     * Returns an integer value representing the protocol we are using
     */
    public int protocolType() {
        return PROTOCOL;
    }

    /**
     * Get the protocol type
     */
    public String getProtocol(int protocolNum) {
        String protocol = protocolNum + " ";

        switch (protocolNum) {
        case UDP_PROTOCOL:
            PROTOCOL = 17;
            return protocol + "(UDP)";
        case TCP_PROTOCOL:
            PROTOCOL = 6;
            return protocol + "(TCP)";
        case ICMP_PROTOCOL:
            PROTOCOL = 1;
            return protocol + "(ICMP)";
        case 88:
            PROTOCOL = 88;
            return protocol + "(IGRP)";
        case 89:
            PROTOCOL = 89;
            return protocol + "(OSPF)";
        default:
            return protocol;
        }
    }

    public void printDecimal(String title, int processType, int unitNum, String binaryChunk) {

        Util util = new Util();

        // Covertbiar string to a decimal value
        int decimal = Integer.parseInt(binaryChunk, 2);
        String result = "";

        // For the header length, it is slightly different,
        // as we need to multiply it by 4
        if (title == "Header Length") {
            // If the header length is greater than 5
            // there is an option
            if (decimal > 5) {
                hasOptions = true;
            }

            decimal = decimal * 4;
            headerLength = decimal;
            result = decimal + util.getUnit(unitNum);
        } else if (title == "Protocol") {
            result = getProtocol(decimal);
        } else {
            result = decimal + util.getUnit(unitNum);
        }

        // Print the values
        String formatted = String.format(type + "%-25s = %20s", title, result);
        System.out.println(formatted);
    }

    /**
     * Obtain the header length of IP Packet
     */
    public int getHeaderLength() {
        return headerLength;
    }

    /**
     * Print the DSCP Values
     * 
     * @param title
     * @param binaryChunk
     */
    public void printDSCP(String title, String binaryChunk, int bitLen) {
        Util util = new Util();

        int[] positions = new int[bitLen - 2];

        // Fill the array with the bit length which we want to show
        for (int i = 0; i < bitLen - 2; i++) {
            positions[i] = i;
        }

        // These are the individual bits for the code point
        String segmentedBits = util.formatBinary(util.showMultiBit(binaryChunk, positions));

        // Codepoint value. Remove the ECN that is why we only take bits 0-5
        int decimal = Integer.parseInt(binaryChunk.substring(0, bitLen - 1), 2);

        String valueTitle = "Differentiated Services Codepoint";

        String formatted = String.format(type + "\t%s = %s: %d", segmentedBits, valueTitle, decimal);
        System.out.println(formatted);
    }

    /**
     * Print the flag details
     * 
     * @param title
     * @param binaryChunk
     */
    public void printFlagsDetails(String title, String binaryChunk) {
        Util util = new Util();

        int offset = 1;
        int offsetEnd = 3;

        // Hold the potential values for printing what the
        // bit value represent
        LinkedHashMap<Integer, ArrayList<Object>> bitTitles = new LinkedHashMap<>();

        // Create a HashMap to store the options for the resulting values
        ArrayList<Object> bit1 = new ArrayList<>();
        bit1.add(true);
        bit1.add("OK to fragment");
        bit1.add("do not fragment");

        ArrayList<Object> bit2 = new ArrayList<>();
        bit2.add(false);
        bit2.add("last fragment");

        // If the Integer is value true, it means there are options
        // Otherwise, there are no options. Meaning there can be more than one value
        bitTitles.put(0, bit1);
        bitTitles.put(1, bit2);

        util.printSingleBitInfo(binaryChunk, type, bitTitles, offset, offsetEnd);
    }

    /**
     * Print the type of service
     */
    public void printTypeOfService(String title, String binaryChunk) {
        Util util = new Util();

        int offset = 3;
        int offsetEnd = 6;

        // Hold the potential values for printing what the
        // bit value represent
        LinkedHashMap<Integer, ArrayList<Object>> bitTitles = new LinkedHashMap<>();

        // Create a HashMap to store the options for the resulting values
        ArrayList<Object> bit1 = new ArrayList<>();
        bit1.add(false);
        bit1.add("normal delay");

        ArrayList<Object> bit2 = new ArrayList<>();
        bit2.add(false);
        bit2.add("normal throughput");

        ArrayList<Object> bit3 = new ArrayList<>();
        bit3.add(false);
        bit3.add("normal reliability");

        // If the Integer is value true, it means there are options
        // Otherwise, there are no options. Meaning there can be more than one value
        bitTitles.put(0, bit1);
        bitTitles.put(1, bit2);
        bitTitles.put(2, bit3);

        // Hold the potential values for printing what the
        // bit value represent
        util.printSingleBitInfo(binaryChunk, type, bitTitles, offset, offsetEnd);
    }

    public void printHeaderContent(String title, int processType, int unitNum, String binaryChunk) {

        Util util = new Util();

        // Print as decimal
        if (processType == 1) {
            printDecimal(title, processType, unitNum, binaryChunk);
        }
        // Print as hexidecimal
        else if (processType == 2) {
            util.printHex(title, processType, unitNum, binaryChunk, type);
        }
        // Obtain IP Address (i.e 192.1.12.90)
        else if (processType == 3) {
            util.printIP(title, processType, unitNum, binaryChunk, type);
        }
        // Printing the options flag
        else if (processType == 4) {
            if (hasOptions) {
                // Print the values
                String formatted = String.format(type + "%s", "yes options");
                System.out.println(formatted);
            } else {
                String formatted = String.format(type + "%s", "no options");
                System.out.println(formatted);
            }
        }
    }
}
