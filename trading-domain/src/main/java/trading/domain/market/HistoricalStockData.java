package trading.domain.market;

import trading.domain.Amount;
import trading.domain.DayCount;

import java.util.ArrayList;
import java.util.List;

public class HistoricalStockData {
    private Amount lastClosingMarketPrice;
    private int risingDaysInSequence = 0;
    private int decliningDaysInSequence = 0;
    private final List<Amount> closingMarketPrices;

    public HistoricalStockData(Amount initialClosingMarketPrice) {
        this.lastClosingMarketPrice = initialClosingMarketPrice;

        this.closingMarketPrices = new ArrayList<>();
        this.closingMarketPrices.add(initialClosingMarketPrice);
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
        this.closingMarketPrices.add(closingMarketPrice);
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

    public Amount getClosingMarketPrice(DayCount lookBehind) {
        // TODO write tests

        // TODO validate lookBehind > 1

        int lastDayIndex = this.closingMarketPrices.size() - 1;
        int specifiedDayIndex = lastDayIndex - lookBehind.getValue() + 1;

        // TODO validate index OK

        return this.closingMarketPrices.get(specifiedDayIndex);
    }

    public Amount getMaximumClosingMarketPrice(DayCount lookBehindPeriod) {
        // TODO Potential refactoring: add method "extractHistory(maxDays)" and remove param lookBehindPeriod from this method
        // should be conducted if more indicators are going to be limited to a certain look behind period

        if(lookBehindPeriod == null) {
            throw new RuntimeException("The look behind period must be specified.");
        }

        if(lookBehindPeriod.isZero()) {
            throw new RuntimeException("The look behind period must not be zero.");
        }

        if(lookBehindPeriod.getValue() < 0) {
            throw new RuntimeException("The look behind period must not be negative.");
        }

        final int numClosingMarketPrices = this.closingMarketPrices.size();

        if(lookBehindPeriod.getValue() > numClosingMarketPrices) {
            throw new RuntimeException("The look behind period exceeds the available market data history.");
        }

        Amount maximumClosingMarketPrice = this.getLastClosingMarketPrice();

        for(int lookBehindDays = 2; lookBehindDays <= lookBehindPeriod.getValue(); lookBehindDays++) {
            int closingMarketPriceIndex = numClosingMarketPrices - lookBehindDays;

            Amount closingMarketPrice = this.closingMarketPrices.get(closingMarketPriceIndex);

            if(closingMarketPrice.getValue() > maximumClosingMarketPrice.getValue()) {
                maximumClosingMarketPrice = closingMarketPrice;
            }
        }

        return maximumClosingMarketPrice;
    }
}
