package trading.market;

import trading.Amount;

public class HistoricalStockData {
    private Amount lastClosingMarketPrice;
    private int risingDaysInSequence = 0;
    private int decliningDaysInSequence = 0;

    public HistoricalStockData(Amount initialClosingMarketPrice) {
        this.lastClosingMarketPrice = initialClosingMarketPrice;
    }

    protected void registerClosedDay(Amount closingMarketPrice) {
        if(closingMarketPrice.getValue() > lastClosingMarketPrice.getValue()) {
            risingDaysInSequence++;
        }
        else {
            risingDaysInSequence = 0;
        }

        if(closingMarketPrice.getValue() < lastClosingMarketPrice.getValue()) {
            decliningDaysInSequence++;
        }
        else {
            decliningDaysInSequence = 0;
        }

        this.lastClosingMarketPrice = closingMarketPrice;
    }

    public Amount getLastClosingMarketPrice() {
        return this.lastClosingMarketPrice;
    }

    public int getRisingDaysInSequence() {
        return this.risingDaysInSequence;
    }

    public int getDecliningDaysInSequence() {
        return this.decliningDaysInSequence;
    }
}
