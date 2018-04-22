package trading.domain;

public abstract class AbstractId {
    private int value;

    public AbstractId(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return ((Integer) this.value).toString();
    }
}
