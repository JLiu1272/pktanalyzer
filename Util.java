import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/*
 * -----------------------------------------------------------------------------
 * A Class for auxillary functions  
 * -----------------------------------------------------------------------------
*/
public class Util {

    private static int SPACE = 32;

    // The minimum number of bytes to print for
    // the data segment
    private static int MAX_DATA_BYTES = 64;

    // FUNCTIONS FOR PRELIMINARY SETUP

    /**
     * Print the bytes in the array
     * 
     * @param Byte[] - Byte Array
     * @return - Nothing
     * @throws IOException
     */
    public void printBytes(Byte[] data) {
        char singleChar;

        // DEBUGGING - Print the file as byte
        for (byte b : data) {
            singleChar = (char) b;
            System.out.print(singleChar);
        }

        // Print a new line
        System.out.println();
    }

    /*
     * Put all the bytes in an array
     */
    public byte[] parseBytes(File file) throws IOException {
        // Place the byte value in an array
        return Files.readAllBytes(file.toPath());
    }

    /**
     * Remove byte values that are spaces. A space is represented by a 32 decimal
     * value.
     */
    public Byte[] stripSpaces(byte[] data) {

        List<Byte> stripped = new ArrayList<Byte>();

        for (int i = 0; i < data.length; i++) {
            if (data[i] != SPACE) {
                stripped.add(data[i]);
            }
        }

        return stripped.toArray(new Byte[stripped.size()]);
    }

    // BINARY CONVERSION UTILITY FUNCTIONS

    /**
     * Convert a single byte to a binary value
     */
    public String byteToBinary(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    public String byteArrToBinary(Byte[] data) {

        String binary = "";

        for (int i = 0; i < data.length; i++) {
            binary += String.format("%8s", Integer.toBinaryString(data[i].byteValue() & 0xFF)).replace(' ', '0');
        }

        return binary;
    }

    /**
     * To help debug the problem easier, we need to format the binary such that for
     * every 4 bits, we add a space, and for every 32 bit, we add a new line
     * 
     * @param binaryChunk
     * @return
     */
    public String formatBinary(String binaryChunk) {

        // The formatted binary string
        String output = "";

        for (int i = 0; i < binaryChunk.length(); i++) {

            output += binaryChunk.charAt(i);

            // For every 32 bits, we add a newline
            if ((i + 1) % 32 == 0 && i != 0) {
                output += "\n";
            }
            // For every 4 bits, we add a space
            else if ((i + 1) % 4 == 0 && i != 0) {
                output += " ";
            }
        }
        return output;
    }

    /**
     * There are certain packet data where each bit represent a certain flag. This
     * function breaks the bit up and determine what each bit represents
     */
    public void printSingleBitInfo(String binaryChunk, String type, LinkedHashMap<Integer, ArrayList<Object>> bitTitles,
            int offset, int offsetEnd) {
        for (int i = offset; i < offsetEnd; i++) {
            // Bit censored
            String formattedBin = formatBinary(showBit(binaryChunk, i));
            ArrayList<Object> bitOptions = bitTitles.get(i - offset);

            Boolean hasOptions = (Boolean) bitOptions.get(0);

            // Figure out what the correct bitType is
            String bitType = (String) bitOptions.get(1);

            // If there is option, print the correct option
            if (hasOptions) {
                int index = Integer.parseInt(String.valueOf(binaryChunk.charAt(i)));
                bitType = (String) bitOptions.get(index + 1);
            }

            // Print the values
            String formatted = String.format(type + "\t %s = %s", formattedBin, bitType);
            System.out.println(formatted);
        }
    }

    /**
     * We may want to obtain a binary value, but only turn certain bits on, while
     * others off
     * 
     * @param binaryChunk
     * @param positions
     * @return
     */
    public String showMultiBit(String binaryChunk, int[] positions) {

        // Create a copy of the binary string, and
        // toggle the bits that we want to show
        String output = "";

        int posIdx = 0;

        for (int i = 0; i < binaryChunk.length(); i++) {
            if (posIdx < positions.length && i == positions[posIdx]) {
                output += binaryChunk.charAt(i);
                posIdx += 1;
            } else {
                output += ".";
            }
        }
        return output;
    }

    /**
     * A function for only showing one bit at a time
     * 
     * @param pos - the position which we want the bit to show
     */
    public String showBit(String binaryChunk, int pos) {

        // The output where all bits that are not pos
        // will become a dot, while the bit we are
        // concerned with will be the bit value
        String output = "";

        // If the position is out of bounds,
        // we throw an error
        if (pos > binaryChunk.length()) {
            System.err.println("Pos index is out of bounds");
            return "";
        }

        for (int i = 0; i < binaryChunk.length(); i++) {
            if (i == pos) {
                output += binaryChunk.charAt(i);
            } else {
                output += ".";
            }
        }
        return output;
    }

    /**
     * Obtain the unit type based on the integer
     */
    public String getUnit(int unitNum) {
        switch (unitNum) {
        case 1:
            return " Bytes";
        case 2:
            return " seconds/hops";
        default:
            return "";
        }
    }

    /**
     * Print the decimal value given the binary value
     */
    public void printDecimal(String title, int processType, int unitNum, String binaryChunk, String type) {

        // Convert string to a decimal value
        Long decimal = Long.parseLong(binaryChunk, 2);

        // Print the values
        String formatted = String.format(type + "%-25s = %20s", title, decimal + getUnit(unitNum));
        System.out.println(formatted);
    }

    /**
     * Print data in hex chunks. Only print out first 64 Bytes,
     */
    public void printDataInHex(Byte[] data, String type) {
        String hexStr = "";

        // The last index of that was seen
        // so that we can slice up the byte chunks
        int lastIdx = 0;

        for (int i = 0; i < data.length; i++) {

            String hex = String.format("%02x", Byte.valueOf(data[i]));
            hexStr += hex;

            if ((i + 1) % 16 == 0 && i != 0 || i == MAX_DATA_BYTES - 1 || i == data.length - 1) {

                Byte[] subHex = Arrays.copyOfRange(data, lastIdx, i + 1);

                // The hex representation without any spaces
                String hexRaw = bytesToHex(subHex);

                String ascii = hexToAscii(hexRaw);
                String formatted = String.format(type + "%25s %20s'", hexStr, "\'" + ascii);
                System.out.println(formatted);

                lastIdx = i;
                hexStr = "";

                // If the data is greater than 64 bit, we don't care about it
                if (i == MAX_DATA_BYTES - 1)
                    break;
            }
            // For every 2 byte, we add a space
            else if ((i + 1) % 2 == 0 && i != 0) {
                hexStr += " ";
            }
        }
    }

    /**
     * Obtain the hex representation given a byte array
     */
    public String bytesToHex(Byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Convert hex to ASCII Format. For values that are out of the range we are
     * concerned about (hex decimal 31 - 127), we add a '.' for it
     * 
     * @return Returns a string of ascii representation of this hex
     */
    public String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        // For every 2 pairs of hex values, we convert it
        // into decimal and then obtain its ASCII value
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            int decimal = Integer.parseInt(str, 16);

            if (decimal > 31 && decimal < 127) {
                output.append((char) decimal);
            } else {
                output.append('.');
            }
        }

        return output.toString();
    }

    public void printHex(String title, int processType, int unitNum, String binaryChunk, String type) {
        // Covertbiar string to a decimal value, then decimal to hex
        int decimal = Integer.parseInt(binaryChunk, 2);
        String hex = "0x" + Integer.toHexString(decimal);

        // Print the values
        String formatted = String.format(type + "%-25s = %20s", title, hex + getUnit(unitNum));
        System.out.println(formatted);
    }

    public void printIP(String title, int processType, int unitNum, String binaryChunk, String type) {
        String ip = "";
        String sep = "";
        Util util = new Util();

        // Keeps track of the last index we landed on,
        // so I can chunk it correctly
        int lastIdx = 0;

        // Each sub component is 8 bit
        for (int i = 0; i < binaryChunk.length() + 1; i++) {
            if (i % 8 == 0 && i != 0) {
                int decimal = Integer.parseInt(binaryChunk.substring(lastIdx, i), 2);
                ip += sep + decimal;
                lastIdx = i;
                sep = ".";
            }
        }
        // Print the values
        String formatted = String.format(type + "%-25s = %20s", title, ip + util.getUnit(unitNum));
        System.out.println(formatted);
    }

    // ETHERNET FUNCTIONS

    /*
     * Get the size of the packet
     */
    private static long getFileSizeBytes(File file) {
        return file.length();
    }
}