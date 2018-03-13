package trading.challenges;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import trading.Amount;
import trading.DayCount;
import trading.ISIN;
import trading.simulation.MultiStockListDataSource;
import trading.simulation.SimulationDriver;
import trading.simulation.SimulationDriverParametersBuilder;
import trading.simulation.SimulationReport;
import trading.strategy.progressive.ProgressiveTradingStrategy;
import trading.strategy.progressive.ProgressiveTradingStrategyParameters;
import trading.strategy.progressive.ProgressiveTradingStrategyParametersBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class ProgressiveChallengesBase {
    protected static Set<ISIN> getISINSs() {
        return HistoricalTestDataProvider.getHistoricalClosingPrices().get(0).getISINs();
    }

    protected SimulationDriverParametersBuilder simulationDriverParametersBuilder;
    protected ProgressiveTradingStrategyParametersBuilder progressiveTradingStrategyParametersBuilder;
    protected SimulationReport simulationReport;

    private MultiStockListDataSource multiStockListDataSource;

    private static List<String> reportLines;

    @BeforeClass
    public static void prepareReporting() {
        reportLines = new ArrayList<>();
    }

    @AfterClass
    public static void endReporting() {
        System.out.println(String.join("\n", reportLines));
        reportLines = null;
    }

    @Before
    public void prepareSimulationSetup() {
        this.simulationDriverParametersBuilder = new SimulationDriverParametersBuilder();

        this.multiStockListDataSource = new MultiStockListDataSource(HistoricalTestDataProvider.getHistoricalClosingPrices());
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
        this.simulationReport = simulationDriver.runSimulation();

        String line = this.simulationReport.getInitialAccountBalance().toString() +
                ";" + this.simulationReport.getFinalAccountBalance().toString();

        String[] reportParameters = this.getReportParameters();

        if(reportParameters != null) {
            for(String reportParameter: reportParameters) {
                line += ";" + reportParameter;
            }
        }

        reportLines.add(line);
    }

    protected String[] getReportParameters() {
        return null;
    }
}
