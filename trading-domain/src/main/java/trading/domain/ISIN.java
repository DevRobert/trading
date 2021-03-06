package trading.domain;

import java.util.Objects;

public class ISIN {
    public static final ISIN MunichRe = new ISIN("DE0008430026");
    public static final ISIN Allianz = new ISIN("DE0008404005");
    public static final ISIN DeutscheBank = new ISIN("DE0005140008");
    public static final ISIN Infineon = new ISIN("DE0006231004");

    private final String text;
    private final int hashCode;

    public String getText() {
        return text;
    }

    public ISIN(String text) {
        if(text == null) {
            throw new DomainException("The ISIN text must be specified.");
        }

        if(text.isEmpty()) {
            throw new DomainException("The ISIN text must not be empty.");
        }

        this.text = text;
        this.hashCode = this.text.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ISIN isin = (ISIN) o;
        return Objects.equals(text, isin.text);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
