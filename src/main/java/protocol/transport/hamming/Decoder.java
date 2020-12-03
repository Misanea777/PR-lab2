package protocol.transport.hamming;

import io.vavr.collection.List;
import io.vavr.collection.Stream;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public class Decoder implements HammingDecoder {

    private final HammingHelper helper = new HammingHelper();
    private final HammingMessageExtractor extractor = new HammingMessageExtractor();

    @Override
    public boolean isValid(EncodedString input) {
        return indexesOfInvalidParityBits(input).isEmpty();
    }

    @Override
    public BinaryString decode(EncodedString input) {
        EncodedString corrected = Match(indexesOfInvalidParityBits(input).isEmpty()).of(
                Case($(true), () -> input),
                Case($(false), () -> withBitFlippedAt(input, indexesOfInvalidParityBits(input).reduce((a, b) -> a + b) - 1))
        );

        return extractor.stripHammingMetadata(corrected);
    }

    private List<Integer> indexesOfInvalidParityBits(EncodedString input) {
        return Stream.iterate(1, i -> i * 2)
                .takeWhile(it -> it < input.getValue().length())
                .filter(it -> helper.parityIndicesSequence(it - 1, input.getValue().length())
                        .map(v -> toBinaryInt(input, v))
                        .fold(toBinaryInt(input, it - 1), (a, b) -> a ^ b) != 0)
                .toList();
    }

    private Integer toBinaryInt(EncodedString input, Integer v) {
        return Integer.valueOf(Character.toString(input.getValue().charAt(v)));
    }

    private EncodedString withBitFlippedAt(EncodedString source, int ind) {
        char it = source.getValue().charAt(ind);
        StringBuilder builder = new StringBuilder(source.getValue());
        builder.setCharAt(ind, it == '0' ? '1' : '0');
        return EncodedString.of(builder.toString());
    }
}
