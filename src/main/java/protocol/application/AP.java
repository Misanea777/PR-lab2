package protocol.application;

import protocol.Helper;
import protocol.session.SP;

import java.io.IOException;

public class AP {
    SP sProtocol;

    public AP(SP sp) {
        this.sProtocol = sp;
    }

    public byte[] recivePacket() throws Exception {
        byte[] message = sProtocol.reciveBytes();
        return processPacket(message);
    }

    private byte[] processPacket(byte[] message) {
        if(message.length == 0) return new byte[0];

        APHeader apHeader = new APHeader(message);
        message = apHeader.extract(message);

        System.out.println("Aplication type = " + apHeader.getApType());
        System.out.println("Aplication len = " + apHeader.getHEADER_LEN());

        if(apHeader.getApType() == APType.CONTROL) {
            ControlType controlType = processControl(message);
            return ("/" + controlType.toString()).getBytes();
        }
        if(apHeader.getApType() == APType.MESSAGE) {
            return processMessage(message);
        }
        return message;
    }

    private ControlType processControl(byte[] message) {
        ControlProtocol controlProtocol = new ControlProtocol(message);
        System.out.println(controlProtocol.getControlType());
        return controlProtocol.getControlType();
    }

    private byte[] processMessage(byte[] message) {
        System.out.println("Message = " + new String(message));
        System.out.println();
        return message;
    }

    public void sendControlPacket(ControlType controlType) throws Exception {
        byte[] control = new ControlProtocol(controlType).construct();
        byte[] header = new APHeader(APType.CONTROL, control.length).construct();
        byte[] res = Helper.concatByteArrays(header, control);
        sProtocol.data(res);
    }

    public void sendMessage(byte[] message) throws Exception {
        byte[] header = new APHeader(APType.MESSAGE, message.length).construct();
        byte[] res = Helper.concatByteArrays(header, message);
        sProtocol.data(res);
    }

    public void sendMessage(String message) throws Exception {
        sendMessage(message.getBytes());
    }

}

class APHeader {
    private APType apType;
    private int HEADER_LEN;

    public APType getApType() {
        return apType;
    }

    public int getHEADER_LEN() {
        return HEADER_LEN;
    }

    public APHeader(APType apType, int len) {
        this.apType = apType;
        this.HEADER_LEN = len;
    }

    public APHeader(byte[] message) {
        if(message[0] == 0) this.apType = APType.CONTROL;
        if(message[0] == 1) this.apType = APType.MESSAGE;
        this.HEADER_LEN = message[1];
    }

    public byte[] extract(byte[] message) {
        return Helper.subArrayOf(message, 2, message.length);
    }

    byte[] construct() {
        byte[] type = new byte[] {2};
        byte[] len = new byte[] {(byte) this.HEADER_LEN};
        if(apType == APType.CONTROL) type = new byte[] {0};
        if(apType == APType.MESSAGE) type = new byte[] {1};
        byte[] res = Helper.concatByteArrays(type, len);
        return res;
    }


}

enum APType {
    CONTROL,
    MESSAGE,

}

