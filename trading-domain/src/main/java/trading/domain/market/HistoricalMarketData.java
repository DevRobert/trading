package trading.domain.market;

import trading.domain.Amount;
import trading.domain.DayCount;
import trading.domain.DomainException;
import trading.domain.ISIN;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HistoricalMarketData {
    private final Map<ISIN, HistoricalStockData> historicalStockDataMap;
    private final ISIN singleISIN;
    private int durationNumDays;
    private MarketPriceSnapshot lastClosingMarketPrices;
    private final Map<LocalDate, Integer> dayIndexByDate;
    private final LocalDate historyBegin;

    public HistoricalMarketData(MarketPriceSnapshot initialClosingMarketPrices) {
        this.historicalStockDataMap = new HashMap<>();

        for(ISIN isin: initialClosingMarketPrices.getISINs()) {
            Amount initialMarketPrice = initialClosingMarketPrices.getMarketPrice(isin);
            HistoricalStockData historicalStockData = new HistoricalStockData(initialMarketPrice);
            this.historicalStockDataMap.put(isin, historicalStockData);
        }

        if(initialClosingMarketPrices.size() == 1) {
            singleISIN = initialClosingMarketPrices.getISINs().stream().findFirst().get();
        }
        else {
            singleISIN = null;
        }

        this.lastClosingMarketPrices = initialClosingMarketPrices;

        this.durationNumDays = 1;

        this.dayIndexByDate = new HashMap();
        this.dayIndexByDate.put(initialClosingMarketPrices.getDate(), this.durationNumDays - 1);
        this.historyBegin = initialClosingMarketPrices.getDate();
    }

    public HistoricalMarketData(ISIN isin, Amount initialClosingMarketPrice, LocalDate date) {
        this(new MarketPriceSnapshotBuilder().setMarketPrice(isin, initialClosingMarketPrice).setDate(date).build());
    }

    public static HistoricalMarketData of(List<MarketPriceSnapshot> marketPriceSnapshotList) {
        if(marketPriceSnapshotList == null) {
            throw new RuntimeException("The market price snapshot list must be specified.");
        }

        if(marketPriceSnapshotList.isEmpty()) {
            throw new RuntimeException("The market price snapshot list must not be empty.");
        }

        HistoricalMarketData historicalMarketData = new HistoricalMarketData(marketPriceSnapshotList.get(0));

        for(int dayIndex = 1; dayIndex < marketPriceSnapshotList.size(); dayIndex++) {
            historicalMarketData.registerClosedDay(marketPriceSnapshotList.get(dayIndex));
        }

        return historicalMarketData;
    }

    public HistoricalStockData getStockData(ISIN isin) {
        HistoricalStockData historicalStockData = this.historicalStockDataMap.get(isin);

        if(historicalStockData == null) {
            throw new UnknownStockException();
        }

        return historicalStockData;
    }

    public Set<ISIN> getAvailableStocks() {
        return this.historicalStockDataMap.keySet();
    }

    public void registerClosedDay(MarketPriceSnapshot closingMarketPrices) {
        if(closingMarketPrices.getDate().isBefore(this.getDate())) {
            throw new DomainException("The specified date must not lie before the date of the last registered market price snapshot.");
        }

        if(closingMarketPrices.getDate().isEqual(this.getDate())) {
            throw new DomainException("The specified date must not equal the date of the last registered market price snapshot.");
        }

        for(Map.Entry<ISIN, HistoricalStockData> entrySet: this.historicalStockDataMap.entrySet()) {
            Amount closingMarketPrice;

            try {
                closingMarketPrice = closingMarketPrices.getMarketPrice(entrySet.getKey());
            }
            catch(UnknownStockException ex) {
                throw new MissingDataException("The market price snapshot must contain prices for all registered stocks.");
            }

            entrySet.getValue().registerClosedDay(closingMarketPrice);
        }

        if(closingMarketPrices.size() != this.historicalStockDataMap.size()) {
            for(ISIN isin: closingMarketPrices.getISINs()) {
                if(this.historicalStockDataMap.containsKey(isin)) {
                    continue;
                }

                this.historicalStockDataMap.put(isin, new HistoricalStockData(closingMarketPrices.getMarketPrice(isin)));
            }
        }

        this.durationNumDays++;
        this.lastClosingMarketPrices = closingMarketPrices;

        this.dayIndexByDate.put(closingMarketPrices.getDate(), this.durationNumDays - 1);
    }

    public void registerClosedDay(Amount closingMarketPrice, LocalDate date) {
        if(this.singleISIN == null) {
            throw new RuntimeException("The single-stock market update function must not be used when multiple stocks registered.");
        }

        MarketPriceSnapshot marketPriceSnapshot = new MarketPriceSnapshotBuilder()
                .setMarketPrice(this.singleISIN, closingMarketPrice)
                .setDate(date)
                .build();

        this.registerClosedDay(marketPriceSnapshot);
    }

    public DayCount getDuration() {
        return new DayCount(this.durationNumDays);
    }

    public MarketPriceSnapshot getLastClosingMarketPrices() {
        return this.lastClosingMarketPrices;
    }

    public LocalDate getDate() {
        return this.lastClosingMarketPrices.getDate();
    }

    public DayCount countDaysAfter(LocalDate date) {
        if(date == null) {
            throw new RuntimeException("The date must be specified.");
        }

        Integer dayIndex = this.dayIndexByDate.get(date);

        if(dayIndex == null) {
            if(date.isBefore(this.historyBegin)) {
                throw new RuntimeException("The given date lies before the market data history time line.");
            }

            if(date.isAfter(this.getLastClosingMarketPrices().getDate())) {
                throw new RuntimeException("The given date lies after the market data history time line.");
            }

            throw new RuntimeException("The given date is unknown.");
        }

        return new DayCount(this.durationNumDays - dayIndex - 1);
    }
}
