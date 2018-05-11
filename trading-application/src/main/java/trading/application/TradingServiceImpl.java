package trading.application;

import org.springframework.stereotype.Component;
import trading.domain.account.Account;
import trading.domain.broker.VirtualBroker;
import trading.domain.market.HistoricalMarketData;
import trading.domain.simulation.MultiStockMarketDataStore;
import trading.domain.simulation.SimulationBuilder;
import trading.domain.strategy.TradingStrategy;
import trading.domain.strategy.TradingStrategyContext;
import trading.domain.strategy.compoundLocalMaximum.CompoundLocalMaximumTradingStrategy;

@Component
public class TradingServiceImpl implements TradingService {
    private final MultiStockMarketDataStore multiStockMarketDataStore;

    public TradingServiceImpl(MultiStockMarketDataStore multiStockMarketDataStore) {
        this.multiStockMarketDataStore = multiStockMarketDataStore;
    }

    @Override
    public TradeList calculateTrades(Account account) {
        HistoricalMarketData historicalMarketData = HistoricalMarketData.of(this.multiStockMarketDataStore.getAllClosingPrices());

        VirtualBroker broker = new VirtualBroker(account, historicalMarketData, TradingConfiguration.getCommissionStrategy());

        TradingStrategyContext tradingStrategyContext = new TradingStrategyContext(account, broker, historicalMarketData);
        TradingStrategy tradingStrategy = new CompoundLocalMaximumTradingStrategy(TradingConfiguration.getParameters(), tradingStrategyContext);

        new SimulationBuilder()
                .setHistoricalMarketData(historicalMarketData)
                .setTradingStrategy(tradingStrategy)
                .setAccount(account)
                .setBroker(broker)
                .beginSimulation();

        return new TradeList(
                historicalMarketData.getDate(),
                broker.getRegisteredOrderRequests()
        );
    }
}
