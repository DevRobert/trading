package trading.strategy;

public class NeverFiresTrigger implements Trigger {
    @Override
    public void notifyDayPassed() {

    }

    @Override
    public boolean checkFires() {
        return false;
    }
}
