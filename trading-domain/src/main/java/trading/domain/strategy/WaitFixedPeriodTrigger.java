package trading.domain.strategy;

import trading.domain.DayCount;
import trading.domain.market.HistoricalMarketData;

public class WaitFixedPeriodTrigger implements Trigger {
    private final HistoricalMarketData historicalMarketData;
    private final int waitDaysBeforeFire;
    private DayCount initialHistoryDuration;

    public WaitFixedPeriodTrigger(HistoricalMarketData historicalMarketData, DayCount waitDaysBeforeFire) {
        this.historicalMarketData = historicalMarketData;
        this.waitDaysBeforeFire = waitDaysBeforeFire.getValue();
        this.initialHistoryDuration = historicalMarketData.getDuration();
    }

    @Override
    public boolean checkFires() {
        int daysPassed = this.historicalMarketData.getDuration().getValue() - this.initialHistoryDuration.getValue();
        return daysPassed == waitDaysBeforeFire;
    }
}
