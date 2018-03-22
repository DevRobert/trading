package trading.challenges;

import trading.Amount;
import trading.DayCount;
import trading.ISIN;
import trading.broker.CommissionStrategies;
import trading.simulation.MultiStockListDataSource;
import trading.simulation.SimulationDriverParameters;
import trading.simulation.SimulationDriverParametersBuilder;
import trading.simulation.SimulationMarketDataSource;
import trading.strategy.localMaximum.DynamicLocalMaximumTradingStrategy;
import trading.strategy.localMaximum.DynamicLocalMaximumTradingStrategyParameters;

import java.util.ArrayList;
import java.util.List;

public class DynamicLocalMaximumChallenge implements Challenge {
    private List<Integer> getRisingIndicatorLookBehindPeriod() {
        List<Integer> result = new ArrayList<>();

        for(int lookBehind = 1; lookBehind < 10; lookBehind += 1) {
            result.add(lookBehind);
        }

        for(int lookBehind = 10; lookBehind <= 120; lookBehind += 2) {
            result.add(lookBehind);
        }

        return result;
    }

    private List<Double> getRisingIndicatorMinRisingPercentage() {
        List<Double> result = new  ArrayList<>();

        for(double value = 0.0001; value < 0.001; value += 0.00002) {
            result.add(value);
        }

        for(double value = 0.001; value < 0.01; value += 0.0002) {
            result.add(value);
        }

        for(double value = 0.01; value < 0.1; value += 0.002) {
            result.add(value);
        }

        for(double value= 0.1; value <= 1; value += 0.02) {
            result.add(value);
        }

        return result;
    }

    @Override
    public List<Object[]> buildParametersForDifferentRuns() {
        List<Object[]> parameters = new ArrayList<>();

        for(ISIN isin: HistoricalTestDataProvider.getISINs()) {
            for(int risingIndicatorLookBehindPeriod: getRisingIndicatorLookBehindPeriod()) {
                for (double risingIndicatorMinRisingPercentage : getRisingIndicatorMinRisingPercentage()) {
                    parameters.add(new Object[]{
                            isin,
                            risingIndicatorLookBehindPeriod,
                            risingIndicatorMinRisingPercentage
                    });
                }
            }
        }

        return parameters;
    }

    @Override
    public SimulationDriverParameters buildSimulationDriverParametersForRun(Object[] runParameters) {
        final ISIN isin = (ISIN) runParameters[0];
        final int risingIndicatorLookBehindPeriod = (int) runParameters[1];
        final double risingIndicatorMinRisingPercentage = (double) runParameters[2];

        SimulationDriverParametersBuilder simulationDriverParametersBuilder = new SimulationDriverParametersBuilder();

        SimulationMarketDataSource simulationMarketDataSource = new MultiStockListDataSource(HistoricalTestDataProvider.getHistoricalClosingPrices(isin));

        simulationDriverParametersBuilder.setSimulationMarketDataSource(simulationMarketDataSource);

        simulationDriverParametersBuilder.setHistoryDuration(new DayCount(120));
        simulationDriverParametersBuilder.setSimulationDuration(new DayCount(1370));
        simulationDriverParametersBuilder.setSeedCapital(new Amount(100000.0));

        DayCount risingBuyTriggerLocalMaximumLookBehindPeriod = new DayCount(2);
        double risingBuyTriggerMinDistanceFromLocalMaximumPercentage = 0.001;
        DayCount decliningBuyTriggerLocalMaximumLookBehindPeriod = new DayCount(9);
        double decliningBuyTriggerMinDistanceFromLocalMaximumPercentage = 0.2;
        double sellTriggerMinDistanceFromMaximumSinceBuyingPercentage = 0.02;

        simulationDriverParametersBuilder.setTradingStrategyFactory(context -> {
            DynamicLocalMaximumTradingStrategyParameters parameters = new DynamicLocalMaximumTradingStrategyParameters(
                    isin,
                    new DayCount(risingIndicatorLookBehindPeriod),
                    risingIndicatorMinRisingPercentage,
                    risingBuyTriggerLocalMaximumLookBehindPeriod,
                    risingBuyTriggerMinDistanceFromLocalMaximumPercentage,
                    decliningBuyTriggerLocalMaximumLookBehindPeriod,
                    decliningBuyTriggerMinDistanceFromLocalMaximumPercentage,
                    sellTriggerMinDistanceFromMaximumSinceBuyingPercentage
            );

            return new DynamicLocalMaximumTradingStrategy(parameters, context);
        });

        simulationDriverParametersBuilder.setCommissionStrategy(CommissionStrategies.getDegiroXetraCommissionStrategy());

        return simulationDriverParametersBuilder.build();
    }
}
