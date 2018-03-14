package trading.challenges;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import trading.DayCount;
import trading.ISIN;
import trading.market.HistoricalStockData;
import trading.strategy.AlwaysFiresTrigger;
import trading.strategy.DelegateTrigger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(Parameterized.class)
public class LocalMaximumChallenges extends ProgressiveChallengesBase {
    private static List<Integer> getLocalMaximumLookBehindPeriods() {
        List<Integer> result = new ArrayList<>();

        for(int i = 1; i <= 120; i++) {
            result.add(i);
        }

        return result;
    }

    private static List<Double> getMinDistanceFromLocalMaximumPercentage() {
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

    @Parameterized.Parameters
    public static List<Object[]> getParameters() {
        List<Object[]> parameters = new ArrayList<>();

        for(ISIN isin: getISINSs()) {
            for(int localMaximumLookBehindPeriod: getLocalMaximumLookBehindPeriods()) {
                for(double minDistanceFromLocalMaximumPercentage: getMinDistanceFromLocalMaximumPercentage()) {
                    parameters.add(new Object[] { isin, localMaximumLookBehindPeriod, minDistanceFromLocalMaximumPercentage });
                }
            }
        }

        return parameters;
    }

    @Parameterized.Parameter(0)
    public ISIN isin;

    @Parameterized.Parameter(1)
    public Integer localMaximumLookBehindPeriod;

    @Parameterized.Parameter(2)
    public Double minDistanceFromLocalMaximumPercentage;

    @Test
    public void buyAfterDistanceFromLocalMaximumReachedAndSellAfterLocalMaximumReachedAgain() {
        DayCount localMaximumLookBehindPeriod = new DayCount(this.localMaximumLookBehindPeriod);
        double minDistanceFromLocalMaxmiumPercentage = this.minDistanceFromLocalMaximumPercentage;

        this.progressiveTradingStrategyParametersBuilder.setISIN(isin);

        AtomicReference<Double> buyLocalMaxmium = new AtomicReference<>((double) 0);

        this.progressiveTradingStrategyParametersBuilder.setBuyTriggerFactory(historicalMarketData -> {
            HistoricalStockData historicalStockData = historicalMarketData.getStockData(isin);

            return new DelegateTrigger(() -> {
                double localMaximum = historicalStockData.getMaximumClosingMarketPrice(localMaximumLookBehindPeriod).getValue();
                double minDelta = localMaximum * minDistanceFromLocalMaxmiumPercentage;
                double maxBuyPrice = localMaximum - minDelta;
                double lastClosingMarketPrice = historicalStockData.getLastClosingMarketPrice().getValue();

                if(lastClosingMarketPrice <= maxBuyPrice) {
                    buyLocalMaxmium.set(localMaximum);
                    return true;
                }
                else {
                    return false;
                }
            });
        });

        this.progressiveTradingStrategyParametersBuilder.setSellTriggerFactory(historicalMarketData -> {
            HistoricalStockData historicalStockData = historicalMarketData.getStockData(isin);

            return new DelegateTrigger(() -> {
                return historicalStockData.getLastClosingMarketPrice().getValue() >= buyLocalMaxmium.get();
            });
        });

        this.progressiveTradingStrategyParametersBuilder.setResetTriggerFactory(historicalMarketData -> new AlwaysFiresTrigger());
    }

    @Override
    protected String[] getReportParameters() {
        return new String[] {
                "LocalMaximum",
                this.isin.toString(),
                this.minDistanceFromLocalMaximumPercentage.toString(),
                this.localMaximumLookBehindPeriod.toString()
        };
    }
}
