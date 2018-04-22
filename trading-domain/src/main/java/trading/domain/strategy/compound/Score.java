package trading.domain.strategy.compound;

public class Score {
    private final double value;
    private final String comment;

    public Score(double value) {
        this(value, null);
    }

    public Score(double value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public double getValue() {
        return this.value;
    }

    public String getComment() {
        return this.comment;
    }
}
