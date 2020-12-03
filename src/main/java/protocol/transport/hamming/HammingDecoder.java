package protocol.transport.hamming;

public interface HammingDecoder {
    static HammingDecoder decoder() {
        return new Decoder();
    }

    BinaryString decode(EncodedString input);

    boolean isValid(EncodedString input);
}