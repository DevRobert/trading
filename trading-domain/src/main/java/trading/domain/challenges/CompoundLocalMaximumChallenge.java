package trading.domain.challenges;

import trading.domain.Amount;
import trading.domain.DayCount;
import trading.domain.broker.CommissionStrategies;
import trading.domain.simulation.MultiStockListDataSource;
import trading.domain.simulation.SimulationDriverParameters;
import trading.domain.simulation.SimulationDriverParametersBuilder;
import trading.domain.simulation.SimulationMarketDataSource;
import trading.domain.strategy.compoundLocalMaximum.CompoundLocalMaximumTradingStrategy;
import trading.domain.strategy.compoundLocalMaximum.CompoundLocalMaximumTradingStrategyParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompoundLocalMaximumChallenge implements Challenge {
    private HistoricalTestDataProvider historicalTestDataProvider;

    public CompoundLocalMaximumChallenge(HistoricalTestDataProvider historicalTestDataProvider) {
        this.historicalTestDataProvider = historicalTestDataProvider;
    }

    private List<Object> getBuyTriggerLocalMaximumLookBehindPeriod() {
        List<Object> result = new ArrayList<>();

        /*
        for(int lookBehind = 1; lookBehind < 10; lookBehind += 1) {
            result.add(lookBehind);
        }

        for(int lookBehind = 10; lookBehind <= 90; lookBehind += 10) {
            result.add(lookBehind);
        }
        */

        result.add(1);
        result.add(5);
        result.add(10);
        result.add(20);
        result.add(30);
        result.add(90);

        return result;
    }

    private List<Object> getBuyTriggerMinDeclineFromLocalMaximumPercentage() {
        return ParameterGenerators.getLowestResolutionDoubles(0.0, 0.1);
    }

    private List<Object> getSellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage() {
        return ParameterGenerators.getLowestResolutionDoubles(0.0, 0.3);
    }

    private List<Object> getActivateTrailingStopLossMinRaiseSinceBuyingPercentage() {
        return ParameterGenerators.getLowestResolutionDoubles(0.008, 1.0);
    }

    private List<Object> getSellTriggerStopLossMinimumDeclineSinceBuyingPercentage() {
        return ParameterGenerators.getLowestResolutionDoubles(0.0, 0.4);
    }

    private List<Object> getMaximumPercentage() {
        List<Object> result = new ArrayList<>();

        result.add(0.05);
        result.add(0.10);
        result.add(0.15);
        result.add(0.20);

        return result;
    }

    /*
    @Override
    public ParameterTupleSource getParametersSource() {
        List<Object[]> parameterTuples = new ArrayList<>();

        parameterTuples.add(new Object[] {
                10, // buyTriggerLocalMaximumLookBehindPeriod = maximum_look_behind
                0.1, // buyTriggerMinDeclineFromLocalMaximumPercentage = maximum_min_decline
                0.07, // sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage = trailing_stop_loss_min_decline
                0.03, // activateTrailingStopLossMinRaiseSinceBuyingPercentage = trailing_stop_loss_activation_min_raise
                0.1, // sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = stop_loss_min_decline
                0.2 // getMaximumPercentage = maximum_percentage
        });

        parameterTuples.add(new Object[] {
                10, // buyTriggerLocalMaximumLookBehindPeriod = maximum_look_behind
                0.05, // buyTriggerMinDeclineFromLocalMaximumPercentage = maximum_min_decline
                0.07, // sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage = trailing_stop_loss_min_decline
                0.03, // activateTrailingStopLossMinRaiseSinceBuyingPercentage = trailing_stop_loss_activation_min_raise
                0.1, // sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = stop_loss_min_decline
                0.2 // getMaximumPercentage = maximum_percentage
        });

        return new PreparedParameterTupleSource(parameterTuples);
    }
    */

        @Override
        public ParameterTupleSource getParametersSource() {
            return new LazyParameterTupleSource(Arrays.asList(
                    this.getBuyTriggerLocalMaximumLookBehindPeriod(),
                    this.getBuyTriggerMinDeclineFromLocalMaximumPercentage(),
                    this.getSellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage(),
                    this.getActivateTrailingStopLossMinRaiseSinceBuyingPercentage(),
                    this.getSellTriggerStopLossMinimumDeclineSinceBuyingPercentage(),
                    this.getMaximumPercentage()
            ));
        }

        @Override
        public SimulationDriverParameters buildSimulationDriverParametersForRun(Object[] runParameters) {
            final int buyTriggerLocalMaximumLookBehindPeriod = (int) runParameters[0];
            final double buyTriggerMinDeclineFromLocalMaximumPercentage = (double) runParameters[1];
            final double sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage = (double) runParameters[2];
            final double activateTrailingStopLossMinRaiseSinceBuyingPercentage = (double) runParameters[3];
        final double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = (double) runParameters[4];
        final double maximumPercentage = (double) runParameters[5];

        SimulationDriverParametersBuilder simulationDriverParametersBuilder = new SimulationDriverParametersBuilder();

        SimulationMarketDataSource simulationMarketDataSource = new MultiStockListDataSource(this.historicalTestDataProvider.getHistoricalClosingPrices());

        simulationDriverParametersBuilder.setSimulationMarketDataSource(simulationMarketDataSource);

        simulationDriverParametersBuilder.setHistoryDuration(new DayCount(120));
        simulationDriverParametersBuilder.setSimulationDuration(new DayCount(1370));
        simulationDriverParametersBuilder.setSeedCapital(new Amount(10000.0));

        simulationDriverParametersBuilder.setTradingStrategyFactory(context -> {
            CompoundLocalMaximumTradingStrategyParameters parameters = new CompoundLocalMaximumTradingStrategyParameters(
                    new DayCount(buyTriggerLocalMaximumLookBehindPeriod),
                    buyTriggerMinDeclineFromLocalMaximumPercentage,
                    sellTriggerTrailingStopLossMinDeclineFromMaximumAfterBuyingPercentage,
                    activateTrailingStopLossMinRaiseSinceBuyingPercentage,
                    sellTriggerStopLossMinimumDeclineSinceBuyingPercentage,
                    maximumPercentage
            );

            return new CompoundLocalMaximumTradingStrategy(parameters, context);
        });

        simulationDriverParametersBuilder.setCommissionStrategy(CommissionStrategies.getDegiroXetraCommissionStrategy());

        return simulationDriverParametersBuilder.build();
    }

    @Override
    public String[] getParameterNames() {
        return new String[] {
                "maximum_look_behind",
                "maximum_min_decline",
                "trailing_stop_loss_min_decline",
                "trailing_stop_loss_activation_min_raise",
                "stop_loss_min_decline",
                "maximum_percentage"
        };
    }
}
