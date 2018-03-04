package trading.market;

import trading.Amount;

public class HistoricalStockData {
    private Amount lastMarketPrice;
    private int risingDaysInSequence = 0;
    private int decliningDaysInSequence = 0;

    public Amount getLastMarketPrice() {
        return this.lastMarketPrice;
    }

    public HistoricalStockData(Amount initialMarketPrice) {
        this.lastMarketPrice = initialMarketPrice;
    }

    public void pushMarketPrice(Amount marketPrice) {
        if(marketPrice.getValue() > lastMarketPrice.getValue()) {
            risingDaysInSequence++;
        }
        else {
            risingDaysInSequence = 0;
        }

        if(marketPrice.getValue() < lastMarketPrice.getValue()) {
            decliningDaysInSequence++;
        }
        else {
            decliningDaysInSequence = 0;
        }

        this.lastMarketPrice = marketPrice;
    }

    public int getRisingDaysInSequence() {
        return risingDaysInSequence;
    }

    public int getDecliningDaysInSequence() {
        return decliningDaysInSequence;
    }
}
