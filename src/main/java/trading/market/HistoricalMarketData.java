package trading.market;

import trading.Amount;
import trading.DayCount;
import trading.ISIN;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HistoricalMarketData {
    private final Map<ISIN, HistoricalStockData> historicalStockDataMap;
    private final ISIN singleISIN;
    private int durationNumDays = 1;

    public HistoricalMarketData(MarketPriceSnapshot initialClosingMarketPrices) {
        this.historicalStockDataMap = new HashMap<>();

        Set<ISIN> isins = initialClosingMarketPrices.getISINs();

        for(ISIN isin: isins) {
            Amount initialMarketPrice = initialClosingMarketPrices.getMarketPrice(isin);
            HistoricalStockData historicalStockData = new HistoricalStockData(initialMarketPrice);
            this.historicalStockDataMap.put(isin, historicalStockData);
        }

        if(isins.size() == 1) {
            singleISIN = isins.stream().findFirst().get();
        }
        else {
            singleISIN = null;
        }
    }

    public HistoricalMarketData(ISIN isin, Amount initialClosingMarketPrice) {
        if(isin == null) {
            throw new RuntimeException("The ISIN must be specified.");
        }

        if(initialClosingMarketPrice == null) {
            throw new RuntimeException("The initialClosingMarketPrice must be specified.");
        }

        this.historicalStockDataMap = new HashMap<>();
        this.historicalStockDataMap.put(isin, new HistoricalStockData(initialClosingMarketPrice));

        this.singleISIN = isin;
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
        Set<ISIN> registeredStocks = this.historicalStockDataMap.keySet();
        Set<ISIN> availableData = closingMarketPrices.getISINs();

        if(!availableData.containsAll(registeredStocks)) {
            throw new MissingDataException("The market price snapshot must contain prices for all registered stocks.");
        }

        for(ISIN isin: registeredStocks) {
            HistoricalStockData historicalStockData = this.historicalStockDataMap.get(isin);
            Amount closingMarketPrice = closingMarketPrices.getMarketPrice(isin);
            historicalStockData.registerClosedDay(closingMarketPrice);
        }

        this.durationNumDays++;
    }

    public void registerClosedDay(Amount closingMarketPrice) {
        if(singleISIN == null) {
            throw new RuntimeException("The single-stock market update function must not be used when multiple stocks registered.");
        }

        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();
        marketPriceSnapshotBuilder.setMarketPrice(this.singleISIN, closingMarketPrice);
        MarketPriceSnapshot marketPriceSnapshot = marketPriceSnapshotBuilder.build();
        this.registerClosedDay(marketPriceSnapshot);
    }

    public DayCount getDuration() {
        return new DayCount(this.durationNumDays);
    }
}
