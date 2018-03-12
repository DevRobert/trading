package trading.challenges;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import trading.Amount;
import trading.DayCount;
import trading.ISIN;
import trading.Transaction;
import trading.market.HistoricalStockData;
import trading.market.MarketPriceSnapshot;
import trading.simulation.*;
import trading.strategy.DelegateTrigger;
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

        System.out.println("Initial account balance: " + simulationReport.getInitialAccountBalance());
        System.out.println("Final account balance: " + simulationReport.getFinalAccountBalance());
        System.out.println(simulationReport.getTransactions().size() + " transactions:");

        for (Transaction transaction : simulationReport.getTransactions()) {
            System.out.println(transaction.toString());
        }

        System.out.println("");
    }

    @Test
    public void buyAndHoldForever() {
        this.progressiveTradingStrategyParametersBuilder.setISIN(ISIN.MunichRe);
        this.progressiveTradingStrategyParametersBuilder.setBuyTriggerFactory(historicalMarketData -> new WaitFixedPeriodTrigger(new DayCount(0)));
        this.progressiveTradingStrategyParametersBuilder.setSellTriggerFactory(historicalMarketData -> new WaitFixedPeriodTrigger(new DayCount(10000)));
        this.progressiveTradingStrategyParametersBuilder.setResetTriggerFactory(historicalMarketData -> new WaitFixedPeriodTrigger(new DayCount(0)));
    }

    @Test
    public void buyAndSellAlternating() {
        this.progressiveTradingStrategyParametersBuilder.setISIN(ISIN.MunichRe);
        this.progressiveTradingStrategyParametersBuilder.setBuyTriggerFactory(historicalMarketData -> new WaitFixedPeriodTrigger(new DayCount(0)));
        this.progressiveTradingStrategyParametersBuilder.setSellTriggerFactory(historicalMarketData -> new WaitFixedPeriodTrigger(new DayCount(0)));
        this.progressiveTradingStrategyParametersBuilder.setResetTriggerFactory(historicalMarketData -> new WaitFixedPeriodTrigger(new DayCount(0)));
    }

    @Test
    public void buyAfterOneRisingDayAndSellAfterOneDecliningDay() {
        ISIN isin = ISIN.MunichRe;

        this.progressiveTradingStrategyParametersBuilder.setISIN(isin);

        this.progressiveTradingStrategyParametersBuilder.setBuyTriggerFactory(historicalMarketData -> {
            HistoricalStockData historicalStockData = historicalMarketData.getStockData(isin);
            return new DelegateTrigger(() -> historicalStockData.getRisingDaysInSequence() >= 1);
        });

        this.progressiveTradingStrategyParametersBuilder.setSellTriggerFactory(historicalMarketData -> {
            HistoricalStockData historicalStockData = historicalMarketData.getStockData(isin);
            return new DelegateTrigger(() -> historicalStockData.getDecliningDaysInSequence() >= 1);
        });

        this.progressiveTradingStrategyParametersBuilder.setResetTriggerFactory(historicalMarketData -> {
            return new WaitFixedPeriodTrigger(new DayCount(0));
        });
    }
}
