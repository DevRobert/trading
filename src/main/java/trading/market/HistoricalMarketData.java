package trading.market;

import trading.Amount;
import trading.ISIN;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HistoricalMarketData {
    private final Map<ISIN, HistoricalStockData> historicalStockDataMap;

    public HistoricalMarketData(MarketPriceSnapshot marketPriceSnapshot) {
        this.historicalStockDataMap = new HashMap<>();

        for(ISIN isin: marketPriceSnapshot.getISINs()) {
            Amount initialMarketPrice = marketPriceSnapshot.getMarketPrice(isin);
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
        // TODO write unit test
        return this.historicalStockDataMap.keySet();
    }
}
