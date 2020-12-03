import protocol.Helper;
import protocol.transport.hamming.BinaryString;
import protocol.transport.hamming.EncodedString;
import protocol.transport.hamming.HammingDecoder;
import protocol.transport.hamming.HammingEncoder;

public class Main {
    public static void main(String[] args) {
//        byte[] m = "idi nah".getBytes();
//        BinaryString s = Helper.toBinStr(m);
//        System.out.println(s.getValue());
//        byte[] or = Helper.toBytes(s);
//        System.out.println(new String(or));

        byte[] m = new byte[]{14,33};
        BinaryString s = Helper.toBinStr(m);
        EncodedString e = HammingEncoder.encoder().encode(s);
        BinaryString ss = HammingDecoder.decoder().decode(e);
        byte[] or = Helper.toBytes(ss);
        Helper.printArray(or);
    }
}


