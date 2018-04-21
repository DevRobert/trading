package trading.domain;

import java.util.Objects;

public class Quantity {
    public final static Quantity Zero = new Quantity(0);

    private int value;

    public int getValue() {
        return value;
    }

    public boolean isZero() {
        return this.value == 0;
    }

    public Quantity(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity = (Quantity) o;
        return value == quantity.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return ((Integer) value).toString();
    }

    public Quantity subtract(Quantity b) {
        return new Quantity(this.value - b.value);
    }
}
