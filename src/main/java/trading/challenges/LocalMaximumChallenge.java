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

        result.add(0.0001);
        result.add(0.00011);
        result.add(0.00012);
        result.add(0.00013);
        result.add(0.00014);
        result.add(0.00015);
        result.add(0.00016);
        result.add(0.00017);
        result.add(0.00018);
        result.add(0.00019);
        result.add(0.0002);
        result.add(0.00021);
        result.add(0.00022);
        result.add(0.00023);
        result.add(0.00024);
        result.add(0.00025);
        result.add(0.00026);
        result.add(0.00027);
        result.add(0.00028);
        result.add(0.00029);
        result.add(0.0003);
        result.add(0.0004);
        result.add(0.0005);
        result.add(0.0006);
        result.add(0.0007);
        result.add(0.0008);
        result.add(0.0009);
        result.add(0.001);
        result.add(0.002);
        result.add(0.003);
        result.add(0.004);
        result.add(0.005);
        result.add(0.006);
        result.add(0.007);
        result.add(0.008);
        result.add(0.009);
        result.add(0.01);
        result.add(0.015);
        result.add(0.02);
        result.add(0.025);
        result.add(0.03);
        result.add(0.035);
        result.add(0.04);
        result.add(0.045);
        result.add(0.05);
        result.add(0.055);
        result.add(0.06);
        result.add(0.065);
        result.add(0.07);
        result.add(0.075);
        result.add(0.08);
        result.add(0.085);
        result.add(0.09);
        result.add(0.095);
        result.add(0.1);
        result.add(0.11);
        result.add(0.12);
        result.add(0.13);
        result.add(0.14);
        result.add(0.15);
        result.add(0.16);
        result.add(0.17);
        result.add(0.18);
        result.add(0.19);
        result.add(0.20);
        result.add(0.21);
        result.add(0.22);
        result.add(0.23);
        result.add(0.24);
        result.add(0.25);

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

        SimulationMarketDataSource simulationMarketDataSource = new MultiStockListDataSource(HistoricalTestDataProvider.getHistoricalClosingPrices());
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
