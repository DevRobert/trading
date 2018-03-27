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

public class DynamicLocalMaximumTimeframeChallenge implements Challenge {
    private List<Integer> getTimeframes() {
        List<Integer> result = new ArrayList<>();

        for(int i = 1; i <= 1000; i++) {
            result.add(i);
        }

        return result;
    }

    @Override
    public ParameterTupleSource buildParametersForDifferentRuns() {
        List<Object[]> parameters = new ArrayList<>();

        for (ISIN isin : HistoricalTestDataProvider.getISINs()) {
            for(Integer timeframe: getTimeframes()) {
                parameters.add(new Object[]{
                        isin,
                        timeframe
                });
            }
        }

        return new PreparedParameterTupleSource(parameters);
    }

    @Override
    public SimulationDriverParameters buildSimulationDriverParametersForRun(Object[] runParameters) {
        final ISIN isin = (ISIN) runParameters[0];
        final Integer timeframe = (Integer) runParameters[1];
        final int risingIndicatorLookBehindPeriod = 60;
        final double risingIndicatorMinRisingPercentage = 0.14;

        //final int risingIndicatorLookBehindPeriod = 102;
        //final double risingIndicatorMinRisingPercentage = 0.024;

        SimulationDriverParametersBuilder simulationDriverParametersBuilder = new SimulationDriverParametersBuilder();

        SimulationMarketDataSource simulationMarketDataSource = new MultiStockListDataSource(HistoricalTestDataProvider.getHistoricalClosingPrices(isin));

        simulationDriverParametersBuilder.setSimulationMarketDataSource(simulationMarketDataSource);

        simulationDriverParametersBuilder.setHistoryDuration(new DayCount(1290 - 200 - timeframe));
        simulationDriverParametersBuilder.setSimulationDuration(new DayCount(200 + 200));
        simulationDriverParametersBuilder.setSeedCapital(new Amount(10000.0));

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
