package trading.domain.market;

import trading.domain.Amount;
import trading.domain.DayCount;
import trading.domain.DomainException;
import trading.domain.ISIN;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HistoricalMarketData {
    private final Map<ISIN, HistoricalStockData> historicalStockDataMap;
    private final ISIN singleISIN;
    private int durationNumDays = 1;
    private final Set<ISIN> isins;
    private MarketPriceSnapshot lastClosingMarketPrices;

    public HistoricalMarketData(MarketPriceSnapshot initialClosingMarketPrices) {
        this.historicalStockDataMap = new HashMap<>();

        this.isins = initialClosingMarketPrices.getISINs();

        for(ISIN isin: this.isins) {
            Amount initialMarketPrice = initialClosingMarketPrices.getMarketPrice(isin);
            HistoricalStockData historicalStockData = new HistoricalStockData(initialMarketPrice);
            this.historicalStockDataMap.put(isin, historicalStockData);
        }

        if(this.isins.size() == 1) {
            singleISIN = isins.stream().findFirst().get();
        }
        else {
            singleISIN = null;
        }

        this.lastClosingMarketPrices = initialClosingMarketPrices;
    }

    public HistoricalMarketData(ISIN isin, Amount initialClosingMarketPrice, LocalDate date) {
        this(new MarketPriceSnapshotBuilder().setMarketPrice(isin, initialClosingMarketPrice).setDate(date).build());
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

        for(ISIN isin: this.isins) {
            HistoricalStockData historicalStockData = this.historicalStockDataMap.get(isin);

            Amount closingMarketPrice;

            try {
                closingMarketPrice = closingMarketPrices.getMarketPrice(isin);
            }
            catch(UnknownStockException ex) {
                throw new MissingDataException("The market price snapshot must contain prices for all registered stocks.");
            }

            historicalStockData.registerClosedDay(closingMarketPrice);
        }

        this.durationNumDays++;
        this.lastClosingMarketPrices = closingMarketPrices;
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
}
