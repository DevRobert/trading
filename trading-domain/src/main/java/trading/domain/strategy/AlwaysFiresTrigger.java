package trading.domain.strategy;

public class AlwaysFiresTrigger implements Trigger {
    @Override
    public boolean checkFires() {
        return true;
    }
}
