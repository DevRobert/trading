package trading.challenges;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import trading.Amount;
import trading.DayCount;
import trading.ISIN;
import trading.market.MarketPriceSnapshot;
import trading.simulation.*;
import trading.strategy.WaitFixedPeriodTrigger;
import trading.strategy.progressive.ProgressiveTradingStrategy;
import trading.strategy.progressive.ProgressiveTradingStrategyParameters;
import trading.strategy.progressive.ProgressiveTradingStrategyParametersBuilder;

import java.util.List;

public class ProgressiveChallenges {
    private static List<MarketPriceSnapshot> HistoricalClosingPrices;

    @BeforeClass
    public static void initializeHistoricalClosingPrices() {
        MongoMultiStockMarketDataStoreParametersBuilder parametersBuilder = new MongoMultiStockMarketDataStoreParametersBuilder();
        parametersBuilder.setDatabase("trading");
        parametersBuilder.setCollection("merged-quotes");
        MongoMultiStockMarketDataStore dataStore = new MongoMultiStockMarketDataStore(parametersBuilder.build());
        HistoricalClosingPrices = dataStore.getAllClosingPrices();
    }

    private SimulationDriverParametersBuilder simulationDriverParametersBuilder;
    private MultiStockListDataSource multiStockListDataSource;
    private ProgressiveTradingStrategyParametersBuilder progressiveTradingStrategyParametersBuilder;

    @Before
    public void prepareSimulationSetup() {
        this.simulationDriverParametersBuilder = new SimulationDriverParametersBuilder();

        this.multiStockListDataSource = new MultiStockListDataSource(HistoricalClosingPrices);
        this.simulationDriverParametersBuilder.setSimulationMarketDataSource(this.multiStockListDataSource);
        this.simulationDriverParametersBuilder.setHistoryDuration(new DayCount(1));
        this.simulationDriverParametersBuilder.setSimulationDuration(new DayCount(1489));
        this.simulationDriverParametersBuilder.setSeedCapital(new Amount(50000.0));

        this.progressiveTradingStrategyParametersBuilder = new ProgressiveTradingStrategyParametersBuilder();
    }

    @After
    public void runSimulation() {
        ProgressiveTradingStrategyParameters progressiveTradingStrategyParameters = this.progressiveTradingStrategyParametersBuilder.build();

        this.simulationDriverParametersBuilder.setTradingStrategyFactory((account, broker, historicalMarketData) ->
                new ProgressiveTradingStrategy(progressiveTradingStrategyParameters, account, broker, historicalMarketData));

        SimulationDriver simulationDriver = new SimulationDriver(this.simulationDriverParametersBuilder.build());
        SimulationReport simulationReport = simulationDriver.runSimulation();
        System.out.println("Final account balance: " + simulationReport.getFinalAccountBalance());
    }

    @Test
    public void buyAndHoldForever() {
        this.progressiveTradingStrategyParametersBuilder.setISIN(ISIN.MunichRe);
        this.progressiveTradingStrategyParametersBuilder.setBuyTrigger(new WaitFixedPeriodTrigger(new DayCount(0)));
        this.progressiveTradingStrategyParametersBuilder.setSellTrigger(new WaitFixedPeriodTrigger(new DayCount(10000)));
        this.progressiveTradingStrategyParametersBuilder.setResetTrigger(new WaitFixedPeriodTrigger(new DayCount(0)));
    }

    @Test
    public void buyAndSellAlternating() {
        this.progressiveTradingStrategyParametersBuilder.setISIN(ISIN.MunichRe);
        this.progressiveTradingStrategyParametersBuilder.setBuyTrigger(new WaitFixedPeriodTrigger(new DayCount(0)));
        this.progressiveTradingStrategyParametersBuilder.setSellTrigger(new WaitFixedPeriodTrigger(new DayCount(0)));
        this.progressiveTradingStrategyParametersBuilder.setResetTrigger(new WaitFixedPeriodTrigger(new DayCount(0)));
    }
}
