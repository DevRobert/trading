package trading;

import java.util.Objects;

public class Amount {
    public static final Amount Zero = new Amount(0.0);

    private final double value;

    public double getValue() {
        return value;
    }

    public Amount(double value) {
        this.value = value;
    }

    public Amount add(Amount other) {
        return new Amount(value + other.value);
    }

    public Amount subtract(Amount other) {
        return new Amount(value - other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount = (Amount) o;
        return Double.compare(amount.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return ((Double) value).toString();
    }
}
