import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sun.nio.cs.ext.DoubleByte.Decoder_EUC_SIM;

import java.util.HashMap;
import java.lang.Math;

public class pktanalyzer {

    // Ethernet Packet
    private static int DESTINATION_LEN = 12;
    private static int SOURCE_LEN = 12;
    private static int ETHER_LEN = 4;

    // IP Packet
    private static int VERSION_LEN = 1;
    private static int HEADER_LENGTH_LEN = 1;
    private static int TOS_LEN = 2;
    private static int IP_TOTAL_LEN = 4;
    private static int IDENTIFICATION_LEN = 4;
    private static int FLAG_LEN = 4;
    private static int TTL_LEN = 2;
    private static int PROTOCOL_LEN = 2;
    private static int HEADER_CHECKSUM_LEN = 4;
    private static int SOURCE_ADDRESS_LEN = 8;
    private static int DESTINATION_ADDRESS_LEN = 8;

    // UDP Packet
    private static int UDP_LENGTH_LEN = 4;

    // TCP Packet
    private static int SEQUENCE_NUM_LEN = 8;
    private static int ACK_NUM_LEN = 8;
    private static int DATA_OFFSET_LEN = 1;
    private static int CONTROL_BITS_LEN = 2;
    private static int WINDOW_LEN = 4;
    private static int URGENT_POINTER_LEN = 4;

    // Common Packet Fields. These are fields that
    // are in more than one type of packet
    private static int CHECKSUM_LEN = 4;
    private static int SOURCE_PORT_LEN = 4;
    private static int DESTINATION_PORT_LEN = 4;
    private static int OPTIONS_LEN = 4;

    private static int startByte = 0;
    private static int endByte = 0;

    public static void main(final String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String line = "";
        String data = "";

        // Init the start and end byte to 0

        while ((line = br.readLine()) != null) {
            line = line.replaceAll("\\s+", "");
            System.out.println(line);
            data += line;
        }

        // String binary = toBinary(data);

        // for (int i = 0; i < binary.length(); i++) {
        // if (i % 4 == 0) {
        // System.out.print(" ");
        // }
        // System.out.print(binary.charAt(i));
        // }

        printEtherHeader(data);
        // printIPHeader(data);

        br.close();
    }

    /*
     * -----------------------------------------------------------------------------
     * Function for TCP
     * -----------------------------------------------------------------------------
     */
    public static void printTCPHeader(String data) {
        String type = "TCP: ";

        System.out.println(type + " ----- " + type + " Header -----");
        System.out.println(type);

        String tcpData = data.substring(startByte);

        endByte = startByte + SOURCE_PORT_LEN;
        System.out.println(type + " Source port = " + hexToDecimal(data.substring(startByte, endByte)));

        startByte += SOURCE_PORT_LEN;
        endByte = startByte + DESTINATION_PORT_LEN;
        System.out.println(type + " Destination port = " + hexToDecimal(data.substring(startByte, endByte)));

        System.out.println(type + " Sequence number = " + hexToDecimal(data.substring(startByte, endByte)));

        System.out.println(type + " Acknowledgement number = " + data.substring(startByte, endByte));

        System.out.println(type + " Data offset = " + data.substring(startByte, endByte) + " bytes");

        System.out.println(type + " Flags = ");

        System.out.println(type + " Window = ");

        System.out.println(type + " Checksum = ");

        System.out.println(type + " Urgent pointer = ");

        System.out.println(type + " No options = ");

        // int dataBytes = udpData.length() / 2;

        // System.out.println(type + "Data: (first " + dataBytes + " bytes)");
        // System.out.println(spaceEveryFour(udpData, type));

    }

    /*
     * -----------------------------------------------------------------------------
     * Function for UDP
     * -----------------------------------------------------------------------------
     */
    public static void printUDPHeader(String data) {
        String type = "UDP: ";

        System.out.println(type + " ----- UDP Header -----");
        System.out.println(type);

        String udpData = data.substring(startByte);

        endByte = startByte + SOURCE_PORT_LEN;
        System.out.println(type + " Source port = " + hexToDecimal(data.substring(startByte, endByte)));

        startByte += SOURCE_PORT_LEN;
        endByte = startByte + DESTINATION_PORT_LEN;
        System.out.println(type + " Destination port = " + hexToDecimal(data.substring(startByte, endByte)));

        startByte += DESTINATION_PORT_LEN;
        endByte = startByte + UDP_LENGTH_LEN;
        System.out.println(type + " Length = " + hexToDecimal(data.substring(startByte, endByte)));

        startByte += UDP_LENGTH_LEN;
        endByte = startByte + CHECKSUM_LEN;
        System.out.println(type + " Checksum = 0x" + data.substring(startByte, endByte));
        System.out.println(type);

        int dataBytes = udpData.length() / 2;

        System.out.println(type + "Data: (first " + dataBytes + " bytes)");
        System.out.println(spaceEveryFour(udpData, type));
    }

    /*
     * -----------------------------------------------------------------------------
     * Function for printing IP header
     * -----------------------------------------------------------------------------
     */

    public static void printIPHeader(String data) {
        String type = "IP: ";

        System.out.println(type + " ----- IP Header -----");
        System.out.println(type);

        // Obtain the version number by taking
        // the 29th bit
        endByte = startByte + VERSION_LEN;
        String version = data.substring(startByte, endByte);
        System.out.println(type + " Version = " + version);

        // Obtain the header length by taking
        // the 30th bit. The header length is in 32 bit increment
        // If there is a 5, it is (32*n)/8 Byte. There are only
        // 2 header length, it is either a 5 or a 6. The minimum
        // is 20
        startByte += VERSION_LEN;
        endByte = startByte + HEADER_LENGTH_LEN;
        int headerLen = (Integer.parseInt(data.substring(startByte, endByte)) * 32) / 8;
        System.out.println(type + " Header Length = " + headerLen);

        // Obtain the type of service.
        // TODO: ---
        startByte += HEADER_LENGTH_LEN;
        endByte = startByte + TOS_LEN;
        String tos = data.substring(startByte, endByte);
        System.out.println(type + " Type of service = 0x" + tos);
        getTOS(tos);

        // Total length of packet
        startByte += TOS_LEN;
        endByte = startByte + IP_TOTAL_LEN;
        String totalLength = data.substring(startByte, endByte);
        System.out.println(type + " Total length = " + hexToDecimal(totalLength) + " bytes");

        // Identification of IP Address
        startByte += IP_TOTAL_LEN;
        endByte = startByte + IDENTIFICATION_LEN;
        String identification = data.substring(startByte, endByte);
        System.out.println(type + " Identification = 0x" + identification + " (" + hexToDecimal(identification) + ")");

        // Obtaining flags
        startByte += IDENTIFICATION_LEN;
        endByte = startByte + FLAG_LEN;
        String flag = data.substring(startByte, endByte);
        System.out.println(type + " Flags = 0x" + flag);
        getFragmentFlag(flag);

        // Obtain the time to live field
        startByte += FLAG_LEN;
        endByte = startByte + TTL_LEN;
        int ttl = hexToDecimal(data.substring(startByte, endByte));
        System.out.println(type + " Time to live = " + ttl + " seconds/hops");

        // Protocol
        startByte += TTL_LEN;
        endByte = startByte + PROTOCOL_LEN;
        int protocol = hexToDecimal(data.substring(startByte, endByte));
        String protocolType = getProtocolType(protocol);
        System.out.println(type + " Protocol = " + protocol + " (" + protocolType + ")");

        // Print the header checksum, a 16 Bit value
        startByte += PROTOCOL_LEN;
        endByte = startByte + HEADER_CHECKSUM_LEN;
        String checkSum = data.substring(startByte, endByte);
        System.out.println(type + " Header checksum = 0x" + checkSum);

        // Source Address, 32 Bit
        startByte += HEADER_CHECKSUM_LEN;
        endByte = startByte + SOURCE_ADDRESS_LEN;
        String sourceAddr = data.substring(startByte, endByte);
        System.out.println(type + " Source: " + getIPAddress(sourceAddr));

        // Destination Address, 32 Bit
        startByte += SOURCE_ADDRESS_LEN;
        endByte = startByte + DESTINATION_ADDRESS_LEN;
        String destAddr = data.substring(startByte, endByte);
        System.out.println(type + " Destination: " + getIPAddress(destAddr));
        startByte += DESTINATION_ADDRESS_LEN;

        // Various options can be included in the header by a particular vendor's
        // implementation of IP.
        // If options are included, the header must be padded with zeroes to fill in any
        // unused octets so
        // that the header is a multiple of 32 bits, and matches the count of bytes in
        // the Internet Header Length (IHL) field.
        // System.out.println(endByte);

        // Print the header information based on what the protocol type is,
        if (protocolType == "UDP") {
            printUDPHeader(data);
        } else if (protocolType == "TCP") {
            printTCPHeader(data);
        }

    }

    /*
     * Get the protocol for the data packet Protocol | Number (Decimal)
     * --------------------------- ICMP | 1 IGMP | 2 TCP | 6 UDP | 17
     */
    private static String getProtocolType(int decimal) {

        if (decimal == 1) {
            return "ICMP";
        } else if (decimal == 2) {
            return "IGMP";
        } else if (decimal == 6) {
            return "TCP";
        } else if (decimal == 17) {
            return "UDP";
        } else {
            return "NA";
        }

    }

    /* Obtain the fragment flags */
    public static void getFragmentFlag(String data) {
        String type = "IP: ";

        String binary = toBinary(data);

        // The first 3 bits of the 16 bit fragment
        // is the flag.
        int flagLen = 3;

        String halfBin = binary.substring(0, binary.length() / 2);

        // Obtain the first fragment and blocking off unused
        // bits with .
        List<Integer> replaceIndex = new ArrayList<Integer>();
        replaceIndex.add(1);
        String firstFragment = createBlockedBin(replaceIndex, halfBin);
        System.out.println(type + "\t" + firstFragment + " = do not fragment");

        // Obtain the last fragment and blocking off unused
        // bits with .
        replaceIndex = new ArrayList<Integer>();
        replaceIndex.add(2);
        String lastFragment = createBlockedBin(replaceIndex, halfBin);
        System.out.println(type + "\t" + lastFragment + " = last fragment");

        // Print the fragment offset
        // The fragment offset is computed based on the remaining 13 bit
        int fragmentOffset = hexToDecimal(binary.substring(flagLen, binary.length()));
        System.out.println(type + " Fragment offset = " + fragmentOffset + " bytes");

    }

    /*
     * Print the Type of Service. The type of service is given using 8 bit.
     **/
    public static void getTOS(String data) {
        String type = "IP: ";

        // Binary representation of TOS
        String tosBin = toBinary(data);

        System.out.println(type + "\t xxx. .... = (precedence)");
        System.out.println(type + "\t ...0 .... = normal delay");
        System.out.println(type + "\t .... 0... = normal throughput");
        System.out.println(type + "\t .... .0.. = normal reliability");
    }

    /*
     * Print the total length of IP Header
     */
    public static int getIPTotalLength(String data) {
        return hexToDecimal(data);
    }

    /*
     * -----------------------------------------------------------------------------
     * Function for printing ethernet header
     * -----------------------------------------------------------------------------
     */

    /*
     * Given the packet data extract ethernet header information and print it
     */
    public static void printEtherHeader(String data) {
        String type = "ETHER:";
        System.out.println(type + " ----- Ether Header -----");
        System.out.println(type);
        System.out.println(type + " Packet size = ");

        // Print the destination
        // and incrementing the start and end byte
        // based on the size of the destination
        endByte = startByte + DESTINATION_LEN;
        System.out.println(data.substring(startByte, endByte));
        System.out.println(type + " Destination = " + getMAC(data, startByte, endByte));

        // Print the source
        // and incrementing the start and end byte
        // based on the size of the source
        startByte += DESTINATION_LEN;
        endByte = startByte + SOURCE_LEN;
        System.out.println(data.substring(startByte, endByte));
        System.out.println(type + " Source = " + getMAC(data, startByte, endByte));

        // Print the ethertype
        startByte += SOURCE_LEN;
        endByte = startByte + ETHER_LEN;
        System.out.println(type + " Ethertype = " + getEtherType(data, startByte, endByte));
        startByte += ETHER_LEN;
    }

    /*
     * Get the EtherType
     **/
    public static String getEtherType(String data, int startByte, int endByte) {
        return data.substring(startByte, endByte) + " (IP)";
    }

    /*
     * Get the destination/source from the packet
     */
    public static String getMAC(String data, int startByte, int endByte) {

        List<Character> destination = new ArrayList<Character>();

        for (int i = startByte; i < endByte; i++) {
            destination.add(data.charAt(i));
            if ((i + 1) % 2 == 0 && i < endByte - 1) {
                destination.add(':');
            }
        }
        return toString(destination);
    }

    /*
     * -----------------------------------------------------------------------------
     * HELPER FUNCTION
     * -----------------------------------------------------------------------------
     */

    /* Convert hexadecimal to binary values */
    private static String toBinary(String data) {

        // The final binary output
        String binary = "";

        // Define a hashmap for mapping hexadecimal to binary values
        HashMap<Character, String> hexToBin = new HashMap<Character, String>();

        hexToBin.put('0', "0000");
        hexToBin.put('1', "0001");
        hexToBin.put('2', "0010");
        hexToBin.put('3', "0011");
        hexToBin.put('4', "0100");
        hexToBin.put('5', "0101");
        hexToBin.put('6', "0110");
        hexToBin.put('7', "0111");
        hexToBin.put('8', "1000");
        hexToBin.put('9', "1001");
        hexToBin.put('A', "1010");
        hexToBin.put('B', "1011");
        hexToBin.put('C', "1100");
        hexToBin.put('D', "1101");
        hexToBin.put('E', "1110");
        hexToBin.put('F', "1111");

        for (int i = 0; i < data.length(); i++) {
            Character val = Character.toUpperCase(data.charAt(i));
            binary += hexToBin.get(val);
        }

        return binary;
    }

    /*
     * Converting a hexadecimal value to a string. The hex value must be in pairs
     */
    public static String hexToString(String hexStr) {

        String spaces = "";

        // The maximum number of bits a line may
        // have is 36. We need to find out how many bits
        // are missing so we can pad those spaces
        int maxLineLen = 39;

        // Determine how many spaces there are. The number
        // of spaces is determined by the length of the binary
        int spaceInbetween = hexStr.length() / 4;

        if (hexStr.length() < 32) {
            for (int i = 0; i < maxLineLen - (hexStr.length() + spaceInbetween); i++) {
                spaces += " ";
            }
        }

        StringBuilder output = new StringBuilder(spaces + "'");

        // Obtain the pair of hex value, convert it into
        // a decimal value, and add the character to output
        // string
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            int decimal = Integer.parseInt(str, 16);

            // The decimal value must be in the range 31 - 127.
            // If it is out of these bounds, it is an incorrect
            // character. We fill the output with .
            if (decimal <= 31 || decimal >= 127) {
                output.append('.');
            } else {
                output.append((char) decimal);
            }
        }

        return output.toString() + "'";
    }

    /*
     * Convert a hexadecimal value into decimal value
     */
    public static int hexToDecimal(String data) {

        // The resulting decimal value
        int decimal = 0;

        // Want to traverse the data from right to left
        int endIdx = data.length() - 1;

        for (int i = endIdx; i >= 0; i--) {

            // If the hexidecimal is a digit, no need
            // to convert it into a number, otherwise
            // convert it to a number that is with respect
            // to the hex value
            char hexVal = data.charAt(i);

            // Hex value is a digit, so we
            // do not need to do any number conversion
            if (Character.isDigit(hexVal)) {
                int val = Character.getNumericValue(hexVal);
                decimal += val * Math.pow(16, endIdx - i);
            } else {
                // Need to convert the alphabet to an int
                int val = ((int) Character.toUpperCase(data.charAt(i))) - 55;
                decimal += val * Math.pow(16, endIdx - i);
            }
        }

        return decimal;
    }

    /*
     * Create a string where, certain bits are blocked off using dots and the bits
     * that we are concerned with is filled with binary values
     */
    public static String createBlockedBin(List<Integer> replaceIndex, String binary) {

        // The resulting binary value for a blocked binary
        String blockedBin = "";
        String sep = "";
        int lstIdx = 0;

        for (int i = 0; i < binary.length(); i++) {

            if (i % 4 == 0) {
                blockedBin += sep;
            }

            if (lstIdx < replaceIndex.size() && replaceIndex.get(lstIdx) == i) {
                blockedBin += binary.charAt(i);
                lstIdx += 1;
            } else {
                blockedBin += ".";
            }

            // Prevents the first 0th value to be a space
            sep = " ";
        }

        return blockedBin;

    }

    /* Given the hexadecimal values, obtain the IP Address */
    public static String getIPAddress(String hex) {
        String ip = "";
        String sep = "";

        for (int i = 0; i < hex.length(); i += 2) {
            ip += sep + hexToDecimal(hex.substring(i, i + 2));
            sep = ".";
        }
        return ip;
    }

    /*
     * For every 4 words, add a space in between
     */
    public static String spaceEveryFour(String data, String type) {

        // The final data string with spaces for every
        // 4 words
        String out = "";

        // The space separator. By initialising the
        // sep to be empty, we ensure that the first
        // 0th index is not filled with a space
        String space = "";
        String newLine = "";

        // Keeps track of the previous line index
        int lastLineIdx = 0;

        // Keep track of where the data last left
        // off so we can take the remaining hex values
        // and convert it to bytes. The index of data byte
        int i;

        for (i = 0; i < data.length(); i++) {

            // For every 4th word, we add a space
            if (i % 4 == 0) {
                out += space;
            }

            // For every 16th bit, we add a new line
            if (i % 32 == 0) {

                if (i != 0) {
                    out += hexToString(data.substring(lastLineIdx, i));
                }

                // Update the lastLineIdx, so that we can obtain
                // the correct substring
                lastLineIdx = i;

                // Add a new line
                out += newLine;
                out += type;
            }

            out += data.charAt(i);

            // init sep to be space
            space = " ";
            newLine = "\n";
        }

        // If there are still remaining hex values that do
        // not have a corresponding hex value
        out += " ";
        out += hexToString(data.substring(lastLineIdx, i));

        return out;
    }

    /*
     * Convert an array of characters into a string
     */
    public static String toString(List<Character> list) {
        StringBuilder builder = new StringBuilder(list.size());
        for (Character ch : list) {
            builder.append(ch);
        }
        return builder.toString();
    }
}