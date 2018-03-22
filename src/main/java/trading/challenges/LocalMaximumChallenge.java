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
import java.util.List;

public class LocalMaximumChallenge implements Challenge {
    private List<Integer> getBuyTriggerLocalMaximumLookBehindPeriods() {
        List<Integer> result = new ArrayList<>();

        for(int lookBehind = 1; lookBehind < 10; lookBehind += 1) {
            result.add(lookBehind);
        }

        for(int lookBehind = 10; lookBehind <= 120; lookBehind += 2) {
            result.add(lookBehind);
        }

        return result;
    }

    private List<Double> getBuyTriggerMinDistanceFromLocalMaximumPercentage() {
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

    private List<Double> getSellTriggerMinDistanceFromMaximumSinceBuyingPercentage() {
        List<Double> result = new  ArrayList<>();

        result.add(0.0);
        result.add(0.02);

        return result;
    }

    @Override
    public List<Object[]> buildParametersForDifferentRuns() {
        List<Object[]> parameters = new ArrayList<>();

        for(ISIN isin: HistoricalTestDataProvider.getISINs()) {
            for(int buyTriggerLocalMaximumLookBehindPeriod: getBuyTriggerLocalMaximumLookBehindPeriods()) {
                for(double buyTriggerMinDistanceFromLocalMaximumPercentage: getBuyTriggerMinDistanceFromLocalMaximumPercentage()) {
                    for(double sellTriggerMinDistanceFromMaximumSinceBuyingPercentage: getSellTriggerMinDistanceFromMaximumSinceBuyingPercentage()) {
                        parameters.add(new Object[] {
                                isin,
                                buyTriggerLocalMaximumLookBehindPeriod,
                                buyTriggerMinDistanceFromLocalMaximumPercentage,
                                sellTriggerMinDistanceFromMaximumSinceBuyingPercentage
                        });
                    }
                }
            }
        }

        return parameters;
    }

    @Override
    public SimulationDriverParameters buildSimulationDriverParametersForRun(Object[] runParameters) {
        final ISIN isin = (ISIN) runParameters[0];
        final int buyTriggerLocalMaximumLookBehindPeriod = (int) runParameters[1];
        final double buyTriggerMinDistanceFromLocalMaximumPercentage = (double) runParameters[2];
        final double sellTriggerMinDistanceFromMaximumSinceBuyingPercentage = (double) runParameters[3];

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
                    buyTriggerMinDistanceFromLocalMaximumPercentage,
                    sellTriggerMinDistanceFromMaximumSinceBuyingPercentage
            );

            return new LocalMaximumTradingStrategy(parameters, context);
        });

        simulationDriverParametersBuilder.setCommissionStrategy(CommissionStrategies.getDegiroXetraCommissionStrategy());

        return simulationDriverParametersBuilder.build();
    }
}
