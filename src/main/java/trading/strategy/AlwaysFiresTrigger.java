package trading.strategy;

public class AlwaysFiresTrigger implements Trigger {
    @Override
    public void notifyDayPassed() {

    }

    @Override
    public boolean checkFires() {
        return true;
    }
}
