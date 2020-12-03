package protocol.application;

public class ControlProtocol {
    private ControlType controlType;

    public ControlType getControlType() {
        return controlType;
    }

    public ControlProtocol(byte[] message) {
        if (message[0] == 0) this.controlType = ControlType.CALL;
        if (message[0] == 1) this.controlType = ControlType.EXIT;
        if (message[0] == 2) this.controlType = ControlType.NOTIFY_CALL;
        if (message[0] == 3) this.controlType = ControlType.NOTIFY_ACCEPT_CALL;
    }

    public byte[] extract(byte[] message) {
        return new byte[0];
    }

    public ControlProtocol(ControlType controlType) {
        this.controlType = controlType;
    }

    public byte[] construct() {
        byte[] type = new byte[]{10};
        if (controlType == ControlType.CALL) type = new byte[]{0};
        if (controlType == ControlType.EXIT) type = new byte[]{1};
        if (controlType == ControlType.NOTIFY_CALL) type = new byte[]{2};
        if (controlType == ControlType.NOTIFY_ACCEPT_CALL) type = new byte[]{3};
        return type;
    }
}
