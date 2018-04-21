package trading.domain;

import java.util.Objects;

public class DayCount {
    private final int value;

    public DayCount(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DayCount dayCount = (DayCount) o;
        return value == dayCount.value;
    }

    @Override
    public int hashCode() {

        return Objects.hash(value);
    }

    public boolean isZero() {
        return this.value == 0;
    }

    @Override
    public String toString() {
        return ((Integer) this.value).toString();
    }
}
