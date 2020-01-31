<<<<<<< HEAD
import java.io.File;
import java.io.IOException;

public class pktanalyzer implements Constants {
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

        if (protocol == UDP_PROTOCOL) {
            UdpHeader udpHeader = new UdpHeader(cursor);
            udpHeader.printUdpHeader(data);
        } else if (protocol == TCP_PROTOCOL) {
            TcpHeader tcpHeader = new TcpHeader(cursor);
            tcpHeader.printTcpHeader(data);
        } else if (protocol == ICMP_PROTOCOL) {
            IcmpHeader icmpHeader = new IcmpHeader(cursor);
            icmpHeader.printIcmpHeader(data);
        } else {
            System.err.println("Protocol is invalid");
        }
    }
}
=======
public static class 

dasfd
>>>>>>> f1021324aa1a8692ad015a049b26b64c0849c857
