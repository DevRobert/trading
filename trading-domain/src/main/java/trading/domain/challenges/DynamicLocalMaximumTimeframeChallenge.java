package trading.domain.challenges;

import trading.domain.Amount;
import trading.domain.DayCount;
import trading.domain.ISIN;
import trading.domain.broker.CommissionStrategies;
import trading.domain.simulation.MultiStockListDataSource;
import trading.domain.simulation.SimulationDriverParameters;
import trading.domain.simulation.SimulationDriverParametersBuilder;
import trading.domain.simulation.SimulationMarketDataSource;
import trading.domain.strategy.localMaximum.DynamicLocalMaximumTradingStrategy;
import trading.domain.strategy.localMaximum.DynamicLocalMaximumTradingStrategyParameters;

import java.util.ArrayList;
import java.util.List;

public class DynamicLocalMaximumTimeframeChallenge implements Challenge {
    private HistoricalTestDataProvider historicalTestDataProvider;

    public DynamicLocalMaximumTimeframeChallenge(HistoricalTestDataProvider historicalTestDataProvider) {
        this.historicalTestDataProvider = historicalTestDataProvider;
    }

    private List<Integer> getTimeframes() {
        List<Integer> result = new ArrayList<>();

        for(int i = 1; i <= 1000; i++) {
            result.add(i);
        }

        return result;
    }

    @Override
    public ParameterTupleSource getParametersSource() {
        List<Object[]> parameters = new ArrayList<>();

        for (ISIN isin : this.historicalTestDataProvider.getISINs()) {
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

        SimulationMarketDataSource simulationMarketDataSource = new MultiStockListDataSource(this.historicalTestDataProvider.getHistoricalClosingPrices(isin));

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

    @Override
    public String[] getParameterNames() {
        return new String[] {
                "isin",
                "risingIndicatorLookBehindPeriod",
                "risingIndicatorMinRisingPercentage",
                "risingBuyTriggerLocalMaximumLookBehindPeriod",
                "risingBuyTriggerMinDistanceFromLocalMaximumPercentage",
                "decliningBuyTriggerLocalMaximumLookBehindPeriod",
                "decliningBuyTriggerMinDistanceFromLocalMaximumPercentage",
                "sellTriggerMinDistanceFromMaximumSinceBuyingPercentage"
        };
    }
}
