import java.util.Arrays;
import java.util.LinkedHashMap;

public class IcmpHeader {

    // The type of header we are printing
    private static String type = "ICMP: ";

    // Keep track of the index where we read up to
    // This need to be a static variable and global to the
    // class as it
    private static int startByte = 0;

    // A hashmap containing the header keys and
    // their byte length
    LinkedHashMap<String, Integer[]> dataTypes = new LinkedHashMap<>();

    public IcmpHeader(int cursor) {
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
        // * 7 = Unknown
        dataTypes.put("Type", new Integer[] { 8, 1, 99 });
        dataTypes.put("Code", new Integer[] { 8, 1, 99 });
        dataTypes.put("Checksum", new Integer[] { 16, 2, 99 });
    }

    public void printIcmpHeader(Byte[] data) {

        // Obtaining the data that are for IP Header
        Byte[] icmpData = Arrays.copyOfRange(data, startByte, data.length);

        System.out.println(type + "----- " + type + "Header  -----");
        System.out.println(type);

        Util util = new Util();

        String binary = util.byteArrToBinary(icmpData);

        // This is to keep track of where we left off from the last bit
        int startBit = 0;

        for (String title : dataTypes.keySet()) {
            int bitLen = dataTypes.get(title)[0];
            int processType = dataTypes.get(title)[1];
            int unitNum = dataTypes.get(title)[2];
            String binaryChunk = binary.substring(startBit, startBit + bitLen);

            printHeaderContent(title, processType, unitNum, binaryChunk);
            // Advance the cursor for keeping track of the bit
            startBit += bitLen;
        }
        System.out.println(type);
    }

    public String getICMPType(int typeNum) {
        switch (typeNum) {
        case 0:
            return " Echo Reply";
        case 1:
            return " Reserved";
        case 2:
            return " Reserved";
        case 3:
            return " Destination Unreachable";
        case 4:
            return " Source Quench";
        case 5:
            return " Redirect Message";
        case 8:
            return " Echo Request";
        case 9:
            return " Router Advertisement";
        case 10:
            return " Router Solicitation";
        case 11:
            return " Time Exceeded";
        default:
            return "";
        }
    }

    /**
     * Print the decimal value given the binary value
     */
    public void printDecimal(String title, int processType, int unitNum, String binaryChunk, String type) {

        // Convert string to a decimal value
        int decimal = Integer.parseInt(binaryChunk, 2);

        // The resulting value
        String result = title == "Type" ? decimal + getICMPType(decimal) : "" + decimal;

        // Print the values
        String formatted = String.format(type + "%-25s = %20s", title, result);
        System.out.println(formatted);
    }

    public void printHeaderContent(String title, int processType, int unitNum, String binaryChunk) {
        Util util = new Util();

        // Print the decimal value
        if (processType == 1) {
            printDecimal(title, processType, unitNum, binaryChunk, type);
        } else if (processType == 2) {
            util.printHex(title, processType, unitNum, binaryChunk, type);
        }
    }
}