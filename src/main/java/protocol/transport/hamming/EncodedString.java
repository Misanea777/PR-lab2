package protocol.transport.hamming;

public class EncodedString {
    private final String value;

    static EncodedString of(String val) {
        return new EncodedString(val);
    }

    public EncodedString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EncodedString that = (EncodedString) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}