package trading.market;

import trading.Amount;
import trading.ISIN;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HistoricalMarketData {
    private final Map<ISIN, HistoricalStockData> historicalStockDataMap;

    public HistoricalMarketData(MarketPriceSnapshot initialClosingMarketPrices) {
        this.historicalStockDataMap = new HashMap<>();

        for(ISIN isin: initialClosingMarketPrices.getISINs()) {
            Amount initialMarketPrice = initialClosingMarketPrices.getMarketPrice(isin);
            HistoricalStockData historicalStockData = new HistoricalStockData(initialMarketPrice);
            this.historicalStockDataMap.put(isin, historicalStockData);
        }
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
    }
}
