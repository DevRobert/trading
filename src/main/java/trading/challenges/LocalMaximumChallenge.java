package trading.challenges;

import trading.Amount;
import trading.DayCount;
import trading.ISIN;
import trading.broker.CommissionStrategies;
import trading.simulation.MultiStockListDataSource;
import trading.simulation.SimulationDriverParameters;
import trading.simulation.SimulationDriverParametersBuilder;
import trading.simulation.SimulationMarketDataSource;
import trading.strategy.localMaximum.LocalMaximumTradingStrategy;
import trading.strategy.localMaximum.LocalMaximumTradingStrategyParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocalMaximumChallenge implements Challenge {
    private List<Object> getISIN() {
        // return HistoricalTestDataProvider.getISINs().stream().collect(Collectors.toList());

        HistoricalTestDataProvider.getISINs();

        List<Object> result = new ArrayList<>();

        result.add(ISIN.MunichRe);

        return result;
    }

    private List<Object> getBuyTriggerLocalMaximumLookBehindPeriod() {
        List<Object> result = new ArrayList<>();

        for(int lookBehind = 1; lookBehind < 10; lookBehind += 1) {
            result.add(lookBehind);
        }

        for(int lookBehind = 10; lookBehind <= 90; lookBehind += 10) {
            result.add(lookBehind);
        }

        return result;
    }

    private List<Object> getBuyTriggerMinDeclineFromLocalMaximumPercentage() {
        return ParameterGenerators.getVeryLowResolutionDoubles(0.0, 0.1);
    }

    private List<Object> getSellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage() {
        return ParameterGenerators.getVeryLowResolutionDoubles(0.0, 0.3);
    }

    private List<Object> getActivateTrailingStopLossMinRaiseSinceBuyingPercentage() {
        return ParameterGenerators.getVeryLowResolutionDoubles(0.008, 1.0);
    }

    private List<Object> getSellTriggerStopLossMinimumDeclineSinceBuyingPercentage() {
        return ParameterGenerators.getVeryLowResolutionDoubles(0.0, 0.4);
    }

    @Override
    public ParameterTupleSource getParametersSource() {
        /*
        List<Object[]> parameterTuples = new ArrayList<>();

        for(ISIN isin: HistoricalTestDataProvider.getISINs()) {
            parameterTuples.add(new Object[] { 7, 0.1, 0.3, 0.04, 0.0, isin});
            parameterTuples.add(new Object[] { 3, 0.09, 0.3, 0.06, 0.07, isin});
        }

        return new PreparedParameterTupleSource(parameterTuples);
        */

        return new LazyParameterTupleSource(Arrays.asList(
                this.getBuyTriggerLocalMaximumLookBehindPeriod(),
                this.getBuyTriggerMinDeclineFromLocalMaximumPercentage(),
                this.getSellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage(),
                this.getActivateTrailingStopLossMinRaiseSinceBuyingPercentage(),
                this.getSellTriggerStopLossMinimumDeclineSinceBuyingPercentage(),
                this.getISIN()
        ));

    }

    @Override
    public SimulationDriverParameters buildSimulationDriverParametersForRun(Object[] runParameters) {
        final int buyTriggerLocalMaximumLookBehindPeriod = (int) runParameters[0];
        final double buyTriggerMinDeclineFromLocalMaximumPercentage = (double) runParameters[1];
        final double sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage = (double) runParameters[2];
        final double activateTrailingStopLossMinRaiseSinceBuyingPercentage = (double) runParameters[3];
        final double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = (double) runParameters[4];
        final ISIN isin = (ISIN) runParameters[5];

        SimulationDriverParametersBuilder simulationDriverParametersBuilder = new SimulationDriverParametersBuilder();

        SimulationMarketDataSource simulationMarketDataSource = new MultiStockListDataSource(HistoricalTestDataProvider.getHistoricalClosingPrices(isin));

        simulationDriverParametersBuilder.setSimulationMarketDataSource(simulationMarketDataSource);

        simulationDriverParametersBuilder.setHistoryDuration(new DayCount(120));
        simulationDriverParametersBuilder.setSimulationDuration(new DayCount(1370));
        simulationDriverParametersBuilder.setSeedCapital(new Amount(10000.0));

        simulationDriverParametersBuilder.setTradingStrategyFactory(context -> {
            LocalMaximumTradingStrategyParameters parameters = new LocalMaximumTradingStrategyParameters(
                    isin,
                    new DayCount(buyTriggerLocalMaximumLookBehindPeriod),
                    buyTriggerMinDeclineFromLocalMaximumPercentage,
                    sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage,
                    activateTrailingStopLossMinRaiseSinceBuyingPercentage,
                    sellTriggerStopLossMinimumDeclineSinceBuyingPercentage
            );

            return new LocalMaximumTradingStrategy(parameters, context);
        });

        simulationDriverParametersBuilder.setCommissionStrategy(CommissionStrategies.getDegiroXetraCommissionStrategy());

        return simulationDriverParametersBuilder.build();
    }

    @Override
    public String[] getParameterNames() {
        return new String[] {
                "maximum_look_behind",
                "maximum_min_decline",
                "stop_loss_min_decline",
                "trailing_stop_loss_activation_min_raise",
                "trailing_stop_loss_min_decline",
                "isin"
        };
    }
}
