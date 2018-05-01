package trading.domain.simulation;

import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;

import java.time.LocalDate;
import java.util.List;

public class SingleStockListDataSource implements SimulationMarketDataSource {
    private final ISIN isin;
    private final List<Amount> closingMarketPrices;
    private final List<LocalDate> dates;
    private int nextClosingMarketPriceIndex;

    public SingleStockListDataSource(ISIN isin, List<Amount> closingMarketPrices, List<LocalDate> dates) {
        if(isin == null) {
            throw new RuntimeException("The ISIN must be specified.");
        }

        if(closingMarketPrices == null) {
            throw new RuntimeException("The closing market prices list must be specified.");
        }

        if(dates == null) {
            throw new RuntimeException("The date list must be specified.");
        }

        if(closingMarketPrices.size() != dates.size()) {
            throw new RuntimeException("The sizes of the price list and the date list must equal.");
        }

        this.isin = isin;
        this.closingMarketPrices = closingMarketPrices;
        this.nextClosingMarketPriceIndex = 0;
        this.dates = dates;
    }

    @Override
    public MarketPriceSnapshot getNextClosingMarketPrices() {
        if(nextClosingMarketPriceIndex >= closingMarketPrices.size()) {
            throw new SimulationMarketDataSourceExhaustedException();
        }

        Amount amount = this.closingMarketPrices.get(nextClosingMarketPriceIndex);
        LocalDate date = this.dates.get(nextClosingMarketPriceIndex);

        nextClosingMarketPriceIndex++;

        return new MarketPriceSnapshotBuilder()
                .setMarketPrice(this.isin, amount)
                .setDate(date)
                .build();
    }
}
