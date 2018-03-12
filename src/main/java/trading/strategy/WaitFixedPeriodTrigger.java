package trading.strategy;

import trading.DayCount;

public class WaitFixedPeriodTrigger implements Trigger {
    private final int waitDaysBeforeFire;
    private int daysPassed = 0;

    public WaitFixedPeriodTrigger(DayCount waitDaysBeforeFire) {
        this.waitDaysBeforeFire = waitDaysBeforeFire.getValue();
    }

    @Override
    public void notifyDayPassed() {
        daysPassed++;
    }

    @Override
    public boolean checkFires() {
        return this.daysPassed == waitDaysBeforeFire;
    }
}
