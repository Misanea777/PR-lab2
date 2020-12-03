package protocol.transport.hamming;

public interface HammingEncoder {

    static HammingEncoder encoder() {
        return new Encoder();
    }

    EncodedString encode(BinaryString input);
}