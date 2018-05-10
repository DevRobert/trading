package trading.domain.strategy.compoundLocalMaximum;

import trading.domain.*;
import trading.domain.account.Account;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.HistoricalStockData;
import trading.domain.strategy.compound.Score;
import trading.domain.strategy.compound.ScoringStrategy;

public class LocalMaximumSellScoringStrategy implements ScoringStrategy {
    private final double activateTrailingStopLossMinRaiseSinceBuyingPercentage;
    private final double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage;
    private final double sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage;

    public LocalMaximumSellScoringStrategy(
            double activateTrailingStopLossMinRaiseSinceBuyingPercentage,
            double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage,
            double sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage) {
        this.activateTrailingStopLossMinRaiseSinceBuyingPercentage = activateTrailingStopLossMinRaiseSinceBuyingPercentage;
        this.sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = sellTriggerStopLossMinimumDeclineSinceBuyingPercentage;
        this.sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage = sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage;
    }

    @Override
    public Score calculateScore(HistoricalMarketData historicalMarketData, Account account, ISIN isin) {
        StringBuilder comment = new StringBuilder();
        boolean sellStock = this.sellStock(historicalMarketData, account, isin, comment);

        comment.append(sellStock ? "Result: Sell!" : "Result: Do not sell!");

        return new Score(sellStock ? 1.0 : 0.0, comment.toString().trim());
    }

    public boolean sellStock(HistoricalMarketData historicalMarketData, Account account, ISIN isin, StringBuilder comment) {
        HistoricalStockData historicalStockData = historicalMarketData.getStockData(isin);
        Amount closingMarketPrice = historicalStockData.getLastClosingMarketPrice();
        Transaction buyTransaction = this.getLastBuyTransaction(account, isin);
        Amount buyPrice = new Amount(buyTransaction.getTotalPrice().getValue() / buyTransaction.getQuantity().getValue());
        DayCount daysPassedAfterBuyingDate = historicalMarketData.countDaysAfter(buyTransaction.getDate());
        DayCount lookBehindPeriod = new DayCount(daysPassedAfterBuyingDate.getValue() + 1);
        Amount maximumMarketPriceSinceBuying = historicalStockData.getMaximumClosingMarketPrice(lookBehindPeriod);

        comment.append("Buy price: " + buyPrice.getValue() + System.lineSeparator());
        comment.append("Closing market price: " + closingMarketPrice.getValue() + System.lineSeparator());
        comment.append("Max. closing since buying: " + maximumMarketPriceSinceBuying.getValue() + System.lineSeparator());

        boolean sell = false;

        if(this.stopLoss(buyPrice, closingMarketPrice, comment)) {
            return true;
        }

        if(!this.trailingStopLossActivated(buyPrice, maximumMarketPriceSinceBuying, comment)) {
           return false;
        }

        return this.trailingStopLoss(closingMarketPrice, maximumMarketPriceSinceBuying, comment);
    }

    private Transaction getLastBuyTransaction(Account account, ISIN isin) {
        Transaction transaction = account.getLastTransaction(isin);

        if(transaction.getTransactionType() != TransactionType.Buy) {
            throw new RuntimeException("Buy transaction expected.");
        }

        return transaction;
    }

    private boolean stopLoss(Amount buyPrice, Amount closingMarketPrice, StringBuilder comment) {
        double sellIfBelow = buyPrice.getValue() * (1.0 - this.sellTriggerStopLossMinimumDeclineSinceBuyingPercentage);
        boolean stopLoss = closingMarketPrice.getValue() < sellIfBelow;

        comment.append("Stop loss if closing price below: " + sellIfBelow + System.lineSeparator());
        comment.append(stopLoss ? "Stop loss!" : "No stop loss");
        comment.append(System.lineSeparator());

        return stopLoss;
    }

    private boolean trailingStopLossActivated(Amount buyPrice, Amount maximumMarketPriceSinceBuying, StringBuilder comment) {
        Amount minActivationPrice = new Amount(buyPrice.getValue() * (1.0 + this.activateTrailingStopLossMinRaiseSinceBuyingPercentage));
        boolean trailingStopLossActivated = maximumMarketPriceSinceBuying.getValue() >= minActivationPrice.getValue();

        comment.append("Trailing stop loss activated if closing price once above: " + minActivationPrice.getValue() + System.lineSeparator());
        comment.append(trailingStopLossActivated ? "Trailing stop loss activated!" : "Trailing stop loss not activated");
        comment.append(System.lineSeparator());

        return trailingStopLossActivated;

    }

    private boolean trailingStopLoss(Amount closingMarketPrice, Amount maximumMarketPriceSinceBuying, StringBuilder comment) {
        double sellIfBelow = maximumMarketPriceSinceBuying.getValue() * (1.0 - this.sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage);
        boolean trailingStopLoss = closingMarketPrice.getValue() < sellIfBelow;

        comment.append("Trailing stop loss if closing price below: " + sellIfBelow + System.lineSeparator());
        comment.append(trailingStopLoss ? "Trailing stop loss!" : "No trailing stop loss");
        comment.append(System.lineSeparator());

        return trailingStopLoss;
    }
}
