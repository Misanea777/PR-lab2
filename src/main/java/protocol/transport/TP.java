package protocol.transport;

import protocol.Helper;

import java.io.IOException;

public class TP {
    private EnhancedUDP udp;
    private boolean connection = false;

    public TP(EnhancedUDP enhancedUDP) {
        this.udp = enhancedUDP;
    }

    public void connect() throws IOException {
        udp.sendBytes(constructPacket(TPHeader.constructHeader(true,false), new byte[0]));
    }


    public void processPacket(byte[] message) throws IOException {
        if(message[0] == 1 && message[1] == 0) processConnReq(message);
        if(message[0] == 1 && message[1] == 1) processConfConnReq(message);
    }

    public void processConfConnReq(byte[] message) {
//        System.out.println("Connected to server");
        this.connection = true;
    }

    public void processConnReq(byte[] message) throws IOException {
//        System.out.println("Connection requested");
        udp.sendBytes(constructPacket(TPHeader.constructHeader(true,true), new byte[0]));
//        System.out.println("Connection Accepted");
        this.connection = true;
    }

    public byte[] constructPacket(byte[] header, byte[] data) {
        byte[] packet = new byte[header.length + data.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(data, 0, packet, header.length, data.length);
        return packet;
    }


    public void sendBytes(byte[] message) throws IOException, InterruptedException {
        if(!this.connection) {
            connect();
        }
        while(!this.connection){
            Thread.sleep(10);
        }
        udp.sendBytes(constructPacket(TPHeader.constructHeader(false,true), message));
    }

    public byte[] reciveBytes() throws IOException {
        byte[] message = udp.reciveBytes();
        processPacket(message);
        return Helper.subArrayOf(message, TPHeader.HEADER_LEN, message.length);
    }
}

class TPHeader {
    final static int HEADER_LEN = 2;
    static byte[] constructHeader(boolean syn, boolean ack) {
        byte[] header = new byte[2];
        if(syn) header[0] = 1;
        else header[0] = 0;
        if(ack) header[1] = 1;
        else  header[1] = 0;

        return header;
    }
}
