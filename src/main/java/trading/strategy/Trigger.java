package trading.strategy;

public interface Trigger {
    void notifyDayPassed();
    boolean checkFires();
}
