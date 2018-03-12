package trading;

import java.util.Objects;

public class ISIN {
    public static final ISIN MunichRe = new ISIN("DE0008430026");
    public static final ISIN Allianz = new ISIN("DE0008404005");

    private final String text;

    public String getText() {
        return text;
    }

    public ISIN(String text) {
        this.text = text;
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
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return this.text;
    }
}
