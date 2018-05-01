package trading.domain.strategy;

public class NotImplementedTrigger implements Trigger {
    public NotImplementedTrigger() {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public boolean checkFires() {
        throw new RuntimeException("Not implemented.");
    }
}
