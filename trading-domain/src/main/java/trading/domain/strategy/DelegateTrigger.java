package trading.domain.strategy;

public class DelegateTrigger implements Trigger {
    private final DelegateTriggerCondition condition;

    public DelegateTrigger(DelegateTriggerCondition condition) {
        if(condition == null) {
            throw new RuntimeException("The condition must be specified.");
        }

        this.condition = condition;
    }

    @Override
    public boolean checkFires() {
        return this.condition.checkFires();
    }
}
