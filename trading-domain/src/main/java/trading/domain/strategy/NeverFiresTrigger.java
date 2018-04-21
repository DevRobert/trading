package trading.domain.strategy;

public class NeverFiresTrigger implements Trigger {
    @Override
    public boolean checkFires() {
        return false;
    }
}
