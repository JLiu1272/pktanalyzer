import java.util.Arrays;
import java.util.LinkedHashMap;

public class UdpHeader {

    // Keep track of the index where we read up to
    // This need to be a static variable and global to the
    // class as it
    private static int startByte = 0;

    // The type of header we are printing
    private static String type = "UDP: ";

    // The minimum number of bytes to print for
    // the data segment
    private static int MAX_DATA_BYTES = 64;

    // A hashmap containing the header keys and
    // their byte length
    LinkedHashMap<String, Integer[]> dataTypes = new LinkedHashMap<>();

    public UdpHeader(int cursor) {

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
        dataTypes.put("Length", new Integer[] { 16, 1, 99 });
        dataTypes.put("Checksum", new Integer[] { 16, 2, 99 });
        dataTypes.put("Data", new Integer[] { 0, 6, 99 });
    }

    public void printUdpHeader(Byte[] data) {

        // Obtaining the data that are for IP Header
        Byte[] udpData = Arrays.copyOfRange(data, startByte, data.length);

        System.out.println(type + "----- " + type + "Header  -----");
        System.out.println(type);

        Util util = new Util();

        String binary = util.byteArrToBinary(udpData);

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
                util.printDataInHex(udpData, type);
            } else {
                printHeaderContent(title, processType, unitNum, binaryChunk);
                // Advance the cursor for keeping track of the bit
                startBit += bitLen;
            }
        }
        System.out.println(type);
    }

    public void printHeaderContent(String title, int processType, int unitNum, String binaryChunk) {
        Util util = new Util();

        // Print the decimal value
        if (processType == 1) {
            util.printDecimal(title, processType, unitNum, binaryChunk, type);
        } else if (processType == 2) {
            util.printHex(title, processType, unitNum, binaryChunk, type);
        }
    }
}