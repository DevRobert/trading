package trading.domain.strategy.compoundLocalMaximum;

import trading.domain.Amount;
import trading.domain.DayCount;
import trading.domain.ISIN;
import trading.domain.account.Account;
import trading.domain.account.Transaction;
import trading.domain.account.TransactionType;
import trading.domain.market.HistoricalMarketData;
import trading.domain.market.HistoricalStockData;
import trading.domain.strategy.compound.Score;
import trading.domain.strategy.compound.ScoringStrategy;

public class LocalMaximumSellScoringStrategy implements ScoringStrategy {
    private final double activateTrailingStopLossMinRaiseSinceBuyingPercentage;
    private final double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage;
    private final double sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage;
    private boolean commentsEnabled;

    public LocalMaximumSellScoringStrategy(
            double activateTrailingStopLossMinRaiseSinceBuyingPercentage,
            double sellTriggerStopLossMinimumDeclineSinceBuyingPercentage,
            double sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage) {
        this.activateTrailingStopLossMinRaiseSinceBuyingPercentage = activateTrailingStopLossMinRaiseSinceBuyingPercentage;
        this.sellTriggerStopLossMinimumDeclineSinceBuyingPercentage = sellTriggerStopLossMinimumDeclineSinceBuyingPercentage;
        this.sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage = sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage;
        this.commentsEnabled = false;
    }

    public void enableComments() {
        this.commentsEnabled = true;
    }

    @Override
    public Score calculateScore(HistoricalMarketData historicalMarketData, Account account, ISIN isin) {
        StringBuilder commentBuilder = null;

        if(this.commentsEnabled) {
            commentBuilder = new StringBuilder();
        }

        boolean sellStock = this.sellStock(historicalMarketData, account, isin, commentBuilder);

        String comment = null;

        if(this.commentsEnabled) {
            commentBuilder.append(sellStock ? "Result: Sell!" : "Result: Do not sell!");
            comment = commentBuilder.toString().trim();
        }

        return new Score(sellStock ? 1.0 : 0.0, comment);
    }

    public boolean sellStock(HistoricalMarketData historicalMarketData, Account account, ISIN isin, StringBuilder commentBuilder) {
        HistoricalStockData historicalStockData = historicalMarketData.getStockData(isin);
        Amount closingMarketPrice = historicalStockData.getLastClosingMarketPrice();
        Transaction buyTransaction = this.getLastBuyTransaction(account, isin);
        Amount buyPrice = new Amount(buyTransaction.getTotalPrice().getValue() / buyTransaction.getQuantity().getValue());
        DayCount daysPassedAfterBuyingDate = historicalMarketData.countDaysAfter(buyTransaction.getDate());
        DayCount lookBehindPeriod = new DayCount(daysPassedAfterBuyingDate.getValue() + 1);
        Amount maximumMarketPriceSinceBuying = historicalStockData.getMaximumClosingMarketPrice(lookBehindPeriod);

        if(this.commentsEnabled) {
            commentBuilder.append("Buy price: " + buyPrice.getValue() + System.lineSeparator());
            commentBuilder.append("Closing market price: " + closingMarketPrice.getValue() + System.lineSeparator());
            commentBuilder.append("Max. closing since buying: " + maximumMarketPriceSinceBuying.getValue() + System.lineSeparator());
        }

        boolean sell = false;

        if(this.stopLoss(buyPrice, closingMarketPrice, commentBuilder)) {
            return true;
        }

        if(!this.trailingStopLossActivated(buyPrice, maximumMarketPriceSinceBuying, commentBuilder)) {
           return false;
        }

        return this.trailingStopLoss(closingMarketPrice, maximumMarketPriceSinceBuying, commentBuilder);
    }

    private Transaction getLastBuyTransaction(Account account, ISIN isin) {
        Transaction transaction = account.getLastTransaction(isin);

        if(transaction.getTransactionType() != TransactionType.Buy) {
            throw new RuntimeException("Buy transaction expected.");
        }

        return transaction;
    }

    private boolean stopLoss(Amount buyPrice, Amount closingMarketPrice, StringBuilder commentBuilder) {
        double sellIfBelow = buyPrice.getValue() * (1.0 - this.sellTriggerStopLossMinimumDeclineSinceBuyingPercentage);
        boolean stopLoss = closingMarketPrice.getValue() < sellIfBelow;

        if(this.commentsEnabled) {
            commentBuilder.append("Stop loss if closing price below: " + sellIfBelow + System.lineSeparator());
            commentBuilder.append(stopLoss ? "Stop loss!" : "No stop loss");
            commentBuilder.append(System.lineSeparator());
        }

        return stopLoss;
    }

    private boolean trailingStopLossActivated(Amount buyPrice, Amount maximumMarketPriceSinceBuying, StringBuilder commentBuilder) {
        Amount minActivationPrice = new Amount(buyPrice.getValue() * (1.0 + this.activateTrailingStopLossMinRaiseSinceBuyingPercentage));
        boolean trailingStopLossActivated = maximumMarketPriceSinceBuying.getValue() >= minActivationPrice.getValue();

        if(this.commentsEnabled) {
            commentBuilder.append("Trailing stop loss activated if closing price once above: " + minActivationPrice.getValue() + System.lineSeparator());
            commentBuilder.append(trailingStopLossActivated ? "Trailing stop loss activated!" : "Trailing stop loss not activated");
            commentBuilder.append(System.lineSeparator());
        }

        return trailingStopLossActivated;

    }

    private boolean trailingStopLoss(Amount closingMarketPrice, Amount maximumMarketPriceSinceBuying, StringBuilder commentBuilder) {
        double sellIfBelow = maximumMarketPriceSinceBuying.getValue() * (1.0 - this.sellTriggerTrailingStopLossMinDeclineSinceMaximumAfterBuyingPercentage);
        boolean trailingStopLoss = closingMarketPrice.getValue() < sellIfBelow;

        if(this.commentsEnabled) {
            commentBuilder.append("Trailing stop loss if closing price below: " + sellIfBelow + System.lineSeparator());
            commentBuilder.append(trailingStopLoss ? "Trailing stop loss!" : "No trailing stop loss");
            commentBuilder.append(System.lineSeparator());
        }

        return trailingStopLoss;
    }
}
