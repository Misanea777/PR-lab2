package protocol.transport;

import protocol.Helper;
import protocol.transport.hamming.BinaryString;
import protocol.transport.hamming.EncodedString;
import protocol.transport.hamming.HammingDecoder;
import protocol.transport.hamming.HammingEncoder;

import java.io.IOException;
import java.net.*;

public class EnhancedUDP {
    private int listeningPort;
    private int receiverPort;
    private InetAddress inetAddress;
    private DatagramSocket datagramSocket;

    public EnhancedUDP(int listeningPort, int receiverPort) {
        try {
            this.listeningPort = listeningPort;
            this.receiverPort = receiverPort;
            inetAddress = InetAddress.getLocalHost();
            datagramSocket = new DatagramSocket(listeningPort);
        } catch (UnknownHostException | SocketException unknownHostException) {

        }
    }

    public void sendPacket(DatagramPacket datagramPacket) throws IOException {
        datagramSocket.send(datagramPacket);
    }

    public DatagramPacket recivePacket() throws IOException {
        byte[] recivedBytes = new byte[1024];
        DatagramPacket recivedPacket = new DatagramPacket(recivedBytes, recivedBytes.length);
        datagramSocket.receive(recivedPacket);
        //this.receiverPort = recivedPacket.getPort();
        return recivedPacket;
    }

    public void sendBytes(byte[] bytes) throws IOException {
        BinaryString b = new BinaryString(Helper.encodeToBitString(bytes));
        EncodedString e = HammingEncoder.encoder().encode(b);
        System.out.println("send");
        System.out.println(e.getValue());
        bytes = Helper.toBytes(e.getValue());
        sendPacket(new DatagramPacket(bytes, bytes.length, inetAddress, receiverPort));
    }
    public byte[] reciveBytes() throws IOException {
        DatagramPacket recivedPacket = recivePacket();
        byte[] rBytes = recivedPacket.getData();
        rBytes = Helper.subArrayOf(rBytes, 0, recivedPacket.getLength());

        EncodedString e = new EncodedString(Helper.decodeUTFCodeAsChar(rBytes));
        System.out.println("recived");
        System.out.println(e.getValue());
        rBytes = Helper.toBytes(HammingDecoder.decoder().decode(e));

        return rBytes;
    }


}
