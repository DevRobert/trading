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
import java.util.Set;
import java.util.stream.Collectors;

public class LocalMaximumChallenge implements Challenge {
    private List<Object> getISIN() {
        return HistoricalTestDataProvider.getISINs().stream().collect(Collectors.toList());
    }

    private List<Object> getHighResolutionDoubles() {
        List<Object> result = new  ArrayList<>();

        result.add(0.0);

        for(double value = 0.0001; value < 0.001; value += 0.00002) {
            result.add(value);
        }

        for(double value = 0.001; value < 0.01; value += 0.0002) {
            result.add(value);
        }

        for(double value = 0.01; value < 0.1; value += 0.002) {
            result.add(value);
        }

        for(double value= 0.1; value <= 0.2; value += 0.02) {
            result.add(value);
        }

        return result;
    }

    private List<Object> getLowResolutionDoubles() {
        List<Object> result = new  ArrayList<>();

        result.add(0.0);

        for(double value = 0.0001; value < 0.001; value += 0.0001) {
            result.add(value);
        }

        for(double value = 0.001; value < 0.01; value += 0.001) {
            result.add(value);
        }

        for(double value = 0.01; value < 0.1; value += 0.01) {
            result.add(value);
        }

        for(double value= 0.1; value <= 0.2; value += 0.1) {
            result.add(value);
        }

        return result;
    }

    private List<Object> getVeryLowResolutionDoubles() {
        List<Object> result = new  ArrayList<>();

        result.add(0.0);

        for(double value = 0.001; value < 0.01; value += 0.002) {
            result.add(value);
        }

        for(double value = 0.01; value < 0.1; value += 0.02) {
            result.add(value);
        }

        for(double value = 0.1; value <= 0.2; value += 0.1) {
            result.add(value);
        }

        return result;
    }

    private List<Object> getBuyTriggerLocalMaximumLookBehindPeriod() {
        List<Object> result = new ArrayList<>();

        for(int lookBehind = 1; lookBehind < 10; lookBehind += 1) {
            result.add(lookBehind);
        }

        for(int lookBehind = 10; lookBehind <= 120; lookBehind += 10) {
            result.add(lookBehind);
        }

        return result;
    }

    private List<Object> getBuyTriggerMinDeclineFromLocalMaximumPercentage() {
        return this.getVeryLowResolutionDoubles();
    }

    private List<Object> getSellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage() {
        return this.getVeryLowResolutionDoubles();
    }

    private List<Object> getActivateTrailingStopLossMinRaiseSinceBuyingPercentage() {
        return this.getVeryLowResolutionDoubles();
    }

    private List<Object> getSellTriggerStopLossMinimumDeclineSinceBuyingPercentage() {
        return this.getVeryLowResolutionDoubles();
    }

    @Override
    public ParameterTupleSource buildParametersForDifferentRuns() {
        return new LazyParameterTupleSource(Arrays.asList(
                this.getISIN(),
                this.getBuyTriggerLocalMaximumLookBehindPeriod(),
                this.getBuyTriggerMinDeclineFromLocalMaximumPercentage(),
                this.getSellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage(),
                this.getActivateTrailingStopLossMinRaiseSinceBuyingPercentage(),
                this.getSellTriggerStopLossMinimumDeclineSinceBuyingPercentage()
        ));
    }

    @Override
    public SimulationDriverParameters buildSimulationDriverParametersForRun(Object[] runParameters) {
        final ISIN isin = (ISIN) runParameters[0];
        final int buyTriggerLocalMaximumLookBehindPeriod = (int) runParameters[1];
        final double buyTriggerMinDeclineFromLocalMaximumPercentage = (double) runParameters[2];
        final double sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage = (double) runParameters[3];
        final double activateTrailingStopLossMinRaiseSinceBuyingPercentage = (double) runParameters[4];
        final double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = (double) runParameters[5];

        SimulationDriverParametersBuilder simulationDriverParametersBuilder = new SimulationDriverParametersBuilder();

        SimulationMarketDataSource simulationMarketDataSource = new MultiStockListDataSource(HistoricalTestDataProvider.getHistoricalClosingPrices(isin));

        simulationDriverParametersBuilder.setSimulationMarketDataSource(simulationMarketDataSource);

        simulationDriverParametersBuilder.setHistoryDuration(new DayCount(120));
        simulationDriverParametersBuilder.setSimulationDuration(new DayCount(1370));
        simulationDriverParametersBuilder.setSeedCapital(new Amount(100000.0));

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
}
