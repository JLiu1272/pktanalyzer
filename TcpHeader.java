import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class TcpHeader {
    // The type of header we are printing
    private static String type = "TCP: ";

    // Keep track of the index where we read up to
    // This need to be a static variable and global to the
    // class as it
    private static int startByte = 0;

    // The minimum number of bytes to print for
    // the data segment
    private static int MAX_DATA_BYTES = 64;

    // A flag for determining whether options exist
    // or not. By default, there are no options
    private static boolean hasOptions = false;

    // A flag for determining whether there is an
    // urgent pointer or not
    private static boolean hasUrgentPointer = false;

    // A hashmap containing the header keys and
    // their byte length
    LinkedHashMap<String, Integer[]> dataTypes = new LinkedHashMap<>();

    public TcpHeader(int cursor) {
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
        // * 5 = Skip field
        // * 6 = Unknown
        dataTypes.put("Source port", new Integer[] { 16, 1, 99 });
        dataTypes.put("Destination port", new Integer[] { 16, 1, 99 });
        dataTypes.put("Sequence number", new Integer[] { 32, 1, 99 });
        dataTypes.put("Acknowledgement number", new Integer[] { 32, 1, 99 });
        dataTypes.put("Data offset", new Integer[] { 4, 1, 1 });
        dataTypes.put("Reserved", new Integer[] { 3, 6, 99 });
        dataTypes.put("Flags", new Integer[] { 9, 2, 1 });
        dataTypes.put("Window", new Integer[] { 16, 1, 99 });
        dataTypes.put("Checksum", new Integer[] { 16, 2, 99 });
        dataTypes.put("Urgent Pointer", new Integer[] { 16, 4, 99 });
        dataTypes.put("Options", new Integer[] { 0, 4, 99 });
        dataTypes.put("Data", new Integer[] { 5, 4, 99 });
    }

    public void printTcpHeader(Byte[] data) {

        // Obtaining the data that are for IP Header
        Byte[] tcpData = Arrays.copyOfRange(data, startByte, data.length);

        System.out.println(type + "----- " + type + "Header  -----");
        System.out.println(type);

        Util util = new Util();

        String binary = util.byteArrToBinary(tcpData);

        // This is to keep track of where we left off from the last bit
        int startBit = 0;

        for (String title : dataTypes.keySet()) {
            int bitLen = dataTypes.get(title)[0];
            int processType = dataTypes.get(title)[1];
            int unitNum = dataTypes.get(title)[2];
            String binaryChunk = binary.substring(startBit, startBit + bitLen);

            // For the data segment we need to print the data a little different
            if (title == "Data") {
                System.out.println(type + "Data: (first " + MAX_DATA_BYTES + " bytes)");
                util.printDataInHex(tcpData, type);
            } else if (title == "Options") {
                String optionsField = hasOptions ? "yes options" : "no options";
                String formatted = String.format(type + "%s", optionsField);
                System.out.println(formatted);
            }
            // For urgent pointer, we determine whether there is an urgent pointer
            // from the flags
            else if (title == "Urgent Pointer") {
                int urgentPointerFlag = hasUrgentPointer ? 1 : 0;
                String formatted = String.format(type + "%-25s = %20d", title, urgentPointerFlag);
                System.out.println(formatted);

                // If there is an urgent pointer, we need to advance the bits
                if (hasUrgentPointer) {
                    startBit += bitLen;
                }

            } else {
                printHeaderContent(title, processType, unitNum, binaryChunk);
                // Advance the cursor for keeping track of the bit
                startBit += bitLen;
            }
        }
        System.out.println(type);
    }

    /**
     * Print the flag details
     * 
     * @param title
     * @param binaryChunk
     */
    public void printFlagsDetails(String title, String binaryChunk) {
        Util util = new Util();

        int offset = 3;
        int offsetEnd = 9;

        // Hold the potential values for printing what the
        // bit value represent
        LinkedHashMap<Integer, ArrayList<Object>> bitTitles = new LinkedHashMap<>();

        // Create a HashMap to store the options for the resulting values
        ArrayList<Object> bit1 = new ArrayList<>();
        bit1.add(true);
        String flagType1 = " urgent pointer";
        bit1.add("No" + flagType1);
        bit1.add("Yes" + flagType1);

        // Determine whether there is an urgent pointer or not
        hasUrgentPointer = util.showBit(binaryChunk, offset) == "1";

        ArrayList<Object> bit2 = new ArrayList<>();
        bit2.add(false);
        bit2.add("Acknowledgement");

        ArrayList<Object> bit3 = new ArrayList<>();
        bit3.add(false);
        bit3.add("Push");

        ArrayList<Object> bit4 = new ArrayList<>();
        bit4.add(true);
        String flagType2 = " rest";
        bit4.add("No" + flagType2);
        bit4.add("Yes" + flagType2);

        ArrayList<Object> bit5 = new ArrayList<>();
        bit5.add(true);
        String flagType3 = " Syn";
        bit5.add("No" + flagType3);
        bit5.add("Yes" + flagType3);

        ArrayList<Object> bit6 = new ArrayList<>();
        bit6.add(true);
        String flagType4 = " Fin";
        bit6.add("No" + flagType4);
        bit6.add("Yes" + flagType4);

        // If the Integer is value true, it means there are options
        // Otherwise, there are no options. Meaning there can be more than one value
        bitTitles.put(0, bit1);
        bitTitles.put(1, bit2);
        bitTitles.put(2, bit3);
        bitTitles.put(3, bit4);
        bitTitles.put(4, bit5);
        bitTitles.put(5, bit6);

        util.printSingleBitInfo(binaryChunk, type, bitTitles, offset, offsetEnd);
    }

    public void printHeaderContent(String title, int processType, int unitNum, String binaryChunk) {
        Util util = new Util();

        // Print the decimal value
        if (processType == 1) {

            if (title == "Data offset") {
                int decimal = Integer.parseInt(binaryChunk, 2);
                // If this byte value is greater than 5, then
                // there is options, otherwise there is no options
                hasOptions = decimal > 5;
            }

            util.printDecimal(title, processType, unitNum, binaryChunk, type);
        } else if (processType == 2) {
            util.printHex(title, processType, unitNum, binaryChunk, type);

            if (title == "Flags") {
                printFlagsDetails(title, binaryChunk);
            }
        }
    }
}