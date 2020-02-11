package trading.domain.account;

import trading.domain.Amount;

public class TaxStrategyImpl implements TaxStrategy {
    private final double taxRate;

    public TaxStrategyImpl(double taxRate) {
        this.taxRate = taxRate;
    }

    @Override
    public Amount calculateTaxImpact(Account account, Transaction transaction) {
        if(transaction instanceof MarketTransaction) {
            return this.calculateTaxImpact(account, (MarketTransaction) transaction);
        }

        if(transaction instanceof DividendTransaction) {
            return this.calculateTaxImpact((DividendTransaction) transaction);
        }

        throw new RuntimeException("Unknown transaction type.");
    }

    private Amount calculateTaxImpact(Account account, MarketTransaction marketTransaction) {
        if(marketTransaction.getTransactionType() == MarketTransactionType.Buy) {
            return Amount.Zero;
        }

        if(marketTransaction.getTransactionType() != MarketTransactionType.Sell) {
            throw new RuntimeException("Unknown market transaction type.");
        }

        // Now handle sell transaction

        MarketTransaction buyTransaction = account.findLastBuyTransaction(marketTransaction.getIsin());

        Position position = account.getPosition(marketTransaction.getIsin());

        if(!position.getQuantity().isZero()) {
            throw new RuntimeException("Partial buy/ sell transactions are not supported.");
        }

        if(!buyTransaction.getQuantity().equals(marketTransaction.getQuantity())) {
            throw new RuntimeException("Partial buy/ sell transactions are not supported.");
        }

        Amount buyTotalPrice = buyTransaction.getTotalPrice();
        Amount sellTotalPrice = marketTransaction.getTotalPrice();

        Amount profit = sellTotalPrice
                .subtract(buyTotalPrice)
                .subtract(buyTransaction.getCommission())
                .subtract(marketTransaction.getCommission());

        return new Amount(profit.getValue() * this.taxRate);
    }

    private Amount calculateTaxImpact(DividendTransaction dividendTransaction) {
        return new Amount(dividendTransaction.getAmount().getValue() * this.taxRate);
    }
}
