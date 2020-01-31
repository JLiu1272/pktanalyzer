import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/*
 * -----------------------------------------------------------------------------
 * A Class for printing ethernet header 
 * -----------------------------------------------------------------------------
*/

public class EtherHeader {

    // Ethernet Packet
    // The lengths are based on half a byte, as in 1 = 4 bits
    private static int DESTINATION_LEN = 6;
    private static int SOURCE_LEN = 6;
    private static int ETHER_LEN = 2;
    private static int ETHER_TOTAL_LEN = 14;

    // The type of header we are printing
    private static String type = "ETHER: ";

    // Keep track of the index where we read up to
    // This need to be a static variable and global to the
    // class as it
    private static int startByte = 0;

    // The file size of the given bin in bytes
    private static long fileSize = 0;

    public EtherHeader(int cursor, long fileLen) {
        startByte = cursor;
        fileSize = fileLen;
    }

    public void printEtherHeader(Byte[] data) {

        System.out.println(type + "----- " + type + " Header -----");
        System.out.println(type);

        // Packet size
        System.out.println(type + "Packet size = " + Long.toString(fileSize) + " bytes");

        // Destination is 6 bytes, so we endByte is the increment of that
        System.out.println(type + "Destination = " + toHex(data, startByte + DESTINATION_LEN, "MAC") + ",");

        // Source is 6 bytes, so we endByte is the increment of that
        System.out.println(type + "Source = " + toHex(data, startByte + SOURCE_LEN, "MAC") + ",");

        // Ethertype is 2 bytes, so we endByte is the increment of that
        System.out.println(type + "Ethertype = " + toHex(data, startByte + ETHER_LEN, "ETHER") + " (IP)");

        System.out.println(type);
    }

    /**
     * Get the MAC Address style from the packet. MAC Address style are data which
     * in in this form byte1:byte2:byte3:... etc... This is used for obtaining the
     * destination and source of the packet
     * 
     * @param data    - The Byte Array obtained from packet
     * @param endByte - The end index of the byte length we wish to obtain
     * @param type    - The type of thing we want to print Types: MAC, ETHER
     * @return A string containing the MAC Address
     */
    public String toHex(Byte[] data, int endByte, String type) {

        String output = "";

        // Obtain the char value of the hexadecimal
        for (int i = startByte; i < endByte; i++) {

            // For every pair, we add a semi colon
            if (type == "MAC" && i != startByte) {
                output += ":";
            }
            output += String.format("%02x", data[i].byteValue());
        }

        // Advance the startByte index cursor by
        // the byte length of output
        startByte = endByte;

        return output;
    }

    /*
     * Get the size of the packet
     */
    private static long getFileSizeBytes(File file) {
        return file.length();
    }
}