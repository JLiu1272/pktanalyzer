import java.io.File;
import java.io.IOException;

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

    // The type of header we are printing
    private static String type = "ETHER: ";

    // Keep track of the index where we read up to
    // This need to be a static variable and global to the
    // class as it
    private static int startByte = 0;

    public EtherHeader(int cursor) {
        startByte = cursor;
    }

    public void printEtherHeader(Byte[] data) {

        System.out.println(type + "----- " + type + " Header -----");
        System.out.println(type);

        // Destination is 6 bytes, so we endByte is the increment of that
        System.out.println(type + "Destination = " + toHex(data, startByte + DESTINATION_LEN, "MAC") + ",");

        // Source is 6 bytes, so we endByte is the increment of that
        System.out.println(type + "Source = " + toHex(data, startByte + SOURCE_LEN, "MAC") + ",");

        // Ethertype is 2 bytes, so we endByte is the increment of that
        System.out.println(type + "Source = " + toHex(data, startByte + ETHER_LEN, "ETHER") + " (IP)");

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

    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
        // Check whether file exist, otherwise do not proceed
        if (!file.exists() || !file.isFile())
            return;

        // Obtain the data in terms of bytes
        Util util = new Util();

        // A cursor indicate where we
        // last left off for the cursor
        int cursor = 0;

        // Hex data with spaces
        byte[] preData = util.parseBytes(file);
        Byte[] data = util.stripSpaces(preData);
        util.printBytes(data);

        // Print the data for ethernet header
        EtherHeader eHeader = new EtherHeader(cursor);
        eHeader.printEtherHeader(data);
        cursor = 14;

        // Print the data for IP Header
        IPHeader ipHeader = new IPHeader(cursor);
        ipHeader.printIPHeader(data);
        cursor = cursor + ipHeader.getHeaderLength();

        // Obtain the protocol type and print accordingly
        int protocol = ipHeader.protocolType();

        if (protocol == pktanalyzer.UDP_PROTOCOL) {
            UdpHeader udpHeader = new UdpHeader(cursor);
            udpHeader.printUdpHeader(data);
        } else if (protocol == pktanalyzer.TCP_PROTOCOL) {
            TcpHeader tcpHeader = new TcpHeader(cursor);
            tcpHeader.printTcpHeader(data);
        } else if (protocol == pktanalyzer.ICMP_PROTOCOL) {
            IcmpHeader icmpHeader = new IcmpHeader(cursor);
            icmpHeader.printIcmpHeader(data);
        }

        // Print the data for TCP Header
        // TcpHeader tcpHeader = new TcpHeader(cursor);
        // tcpHeader.printTcpHeader(data);

    }
}