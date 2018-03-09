package trading.strategy;

import trading.DayCount;

public class WaitFixedPeriodTrigger implements Trigger {
    private final int waitDaysBeforeFire;
    private int daysPassed = 0;
    private boolean activated;

    public WaitFixedPeriodTrigger(DayCount waitDaysBeforeFire) {
        this.waitDaysBeforeFire = waitDaysBeforeFire.getValue();
        this.activated = false;
    }

    @Override
    public void activateTrigger() {
        this.activated = true;
        this.daysPassed = 0;
    }

    @Override
    public void notifyDayPassed() {
        daysPassed++;
    }

    @Override
    public boolean checkFires() {
        return this.activated && this.daysPassed == waitDaysBeforeFire;
    }
}
