package trading.strategy;

public interface Trigger {
    void activateTrigger();
    void notifyDayPassed();
    boolean checkFires();
}
