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
    private List<Integer> getLocalMaximumLookBehindPeriods() {
        List<Integer> result = new ArrayList<>();

        final int start = 1;
        final int end = 120;

        for(int lookBehind = start; lookBehind <= end; lookBehind++) {
            result.add(lookBehind);
        }

        return result;
    }

    private List<Double> getMinDistanceFromLocalMaximumPercentage() {
        List<Double> result = new  ArrayList<>();

        for(double value = 0.0001; value < 0.001; value += 0.00001) {
            result.add(value);
        }

        for(double value = 0.001; value < 0.01; value += 0.0001) {
            result.add(value);
        }

        for(double value = 0.01; value < 0.1; value += 0.001) {
            result.add(value);
        }

        for(double value= 0.1; value <= 1; value += 0.01) {
            result.add(value);
        }

        return result;
    }

    @Override
    public List<Object[]> buildParametersForDifferentRuns() {
        List<Object[]> parameters = new ArrayList<>();

        for(ISIN isin: HistoricalTestDataProvider.getISINs()) {
            for(int localMaximumLookBehindPeriod: getLocalMaximumLookBehindPeriods()) {
                for(double minDistanceFromLocalMaximumPercentage: getMinDistanceFromLocalMaximumPercentage()) {
                    parameters.add(new Object[] {
                            isin,
                            localMaximumLookBehindPeriod,
                            minDistanceFromLocalMaximumPercentage
                    });
                }
            }
        }

        return parameters;
    }

    @Override
    public SimulationDriverParameters buildSimulationDriverParametersForRun(Object[] runParameters) {
        final ISIN isin = (ISIN) runParameters[0];
        final int localMaximumLookBehindPeriod = (int) runParameters[1];
        final double minDistanceFromLocalMaximumPercentage = (double) runParameters[2];

        SimulationDriverParametersBuilder simulationDriverParametersBuilder = new SimulationDriverParametersBuilder();

        SimulationMarketDataSource simulationMarketDataSource = new MultiStockListDataSource(HistoricalTestDataProvider.getHistoricalClosingPrices(isin));

        simulationDriverParametersBuilder.setSimulationMarketDataSource(simulationMarketDataSource);

        simulationDriverParametersBuilder.setHistoryDuration(new DayCount(120));
        simulationDriverParametersBuilder.setSimulationDuration(new DayCount(1370));
        simulationDriverParametersBuilder.setSeedCapital(new Amount(50000.0));

        simulationDriverParametersBuilder.setTradingStrategyFactory(context -> {
            LocalMaximumTradingStrategyParameters parameters = new LocalMaximumTradingStrategyParameters(
                isin, new DayCount(localMaximumLookBehindPeriod), minDistanceFromLocalMaximumPercentage
            );

            return new LocalMaximumTradingStrategy(parameters, context);
        });

        simulationDriverParametersBuilder.setCommissionStrategy(CommissionStrategies.getDegiroXetraCommissionStrategy());

        return simulationDriverParametersBuilder.build();
    }
}
