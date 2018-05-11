package trading.domain.strategy.compound;

import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Quantity;
import trading.domain.broker.CommissionStrategy;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.strategy.AffordableQuantityCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyStocksSelector {
    private final Score minimumBuyScore;
    private final double maximumPercentage;

    public BuyStocksSelector(Score minimumBuyScore, double maximumPercentage) {
        this.minimumBuyScore = minimumBuyScore;
        this.maximumPercentage = maximumPercentage;
    }

    public Map<ISIN, Quantity> selectStocks(Amount totalCapital, Amount availableMoney, Scores stockScores, MarketPriceSnapshot marketPrices, CommissionStrategy commissionStrategy, Map<ISIN, Quantity> currentStocks) {
        ISIN[] allIsins = stockScores.getIsinsOrderByScoreDescending();
        Map<ISIN, Quantity> selectedStocks = this.initializeSelectedStocks(allIsins);
        ISIN[] isins = this.selectPossibleStocks(allIsins, stockScores, currentStocks);

        double maximumMoneyPerStock = this.maximumPercentage * totalCapital.getValue();

        AffordableQuantityCalculator affordableQuantityCalculator = new AffordableQuantityCalculator();

        double totalScore = 0.0;

        for(ISIN isin: isins) {
            Score score = stockScores.get(isin);
            totalScore += score.getValue();
        }

        for(ISIN isin: isins) {
            Score score = stockScores.get(isin);
            double amount = score.getValue() / totalScore;
            double availableMoneyForStock = amount * availableMoney.getValue();
            availableMoneyForStock = Math.min(availableMoneyForStock, maximumMoneyPerStock);

            Amount lastMarketPrice = marketPrices.getMarketPrice(isin);
            Quantity buyQuantity = affordableQuantityCalculator.calculateAffordableQuantity(new Amount(availableMoneyForStock), lastMarketPrice, commissionStrategy);
            selectedStocks.put(isin, buyQuantity);

            Amount totalPrice = lastMarketPrice.multiply(buyQuantity);
            Amount commission = commissionStrategy.calculateCommission(totalPrice);

            totalScore -= score.getValue();
            availableMoney = availableMoney.subtract(totalPrice.add(commission));
        }

        return selectedStocks;
    }

    private Map<ISIN, Quantity> initializeSelectedStocks(ISIN[] allIsins) {
        Map<ISIN, Quantity> selectedStocks = new HashMap<>();

        for(ISIN isin: allIsins) {
            selectedStocks.put(isin, Quantity.Zero);
        }

        return selectedStocks;
    }

    private ISIN[] selectPossibleStocks(ISIN[] allIsins, Scores stockScores, Map<ISIN, Quantity> currentStocks) {
        List<ISIN> possibleStocks = new ArrayList<>(allIsins.length);

        for(ISIN isin: allIsins) {
            Score score = stockScores.get(isin);

            if(score.getValue() < this.minimumBuyScore.getValue()) {
                continue;
            }

            if(currentStocks.containsKey(isin)) {
                continue;
            }

            possibleStocks.add(isin);
        }

        return possibleStocks.toArray(new ISIN[0]);
    }
}
