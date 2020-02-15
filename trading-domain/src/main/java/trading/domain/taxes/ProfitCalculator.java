package trading.domain.taxes;

import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.ISIN;
import trading.domain.account.*;

import java.util.HashMap;
import java.util.Map;

public class ProfitCalculator {
    private Map<ISIN, MarketTransaction> lastOpenBuyTransactionByIsin = new HashMap<>();

    public Profit registerTransactionAndCalculateTransactionProfit(Transaction transaction) {
        if(transaction instanceof MarketTransaction) {
            return this.registerMarketTransactionAndCalculateTransactionProfit((MarketTransaction) transaction);
        }

        if(transaction instanceof DividendTransaction) {
            return this.registerDividendTransactionAndCalculateTransactionProfit((DividendTransaction) transaction);
        }

        if(transaction instanceof TaxPaymentTransaction) {
            return this.registerTaxPaymentTransactionAndCalculateTransactionProfit((TaxPaymentTransaction) transaction);
        }

        throw new RuntimeException("Unknown transaction type.");
    }

    private Profit registerMarketTransactionAndCalculateTransactionProfit(MarketTransaction marketTransaction) {
        if(marketTransaction.getTransactionType() == TransactionType.Buy) {
            return this.registerBuyTransactionAndCalculateTransactionProfit(marketTransaction);
        }

        if(marketTransaction.getTransactionType() == TransactionType.Sell) {
            return this.registerSellTransactionAndCalculateTransactionProfit(marketTransaction);
        }

        throw new RuntimeException("Unknown transaction type.");
    }

    private Profit registerBuyTransactionAndCalculateTransactionProfit(MarketTransaction buyTransaction) {
        if(this.lastOpenBuyTransactionByIsin.containsKey(buyTransaction.getIsin())) {
            throw new DomainException("Consecutive buy transactions are not supported.");
        }

        this.lastOpenBuyTransactionByIsin.put(buyTransaction.getIsin(), buyTransaction);
        return null;
    }

    private Profit registerSellTransactionAndCalculateTransactionProfit(MarketTransaction sellTransaction) {
        MarketTransaction buyTransaction = this.lastOpenBuyTransactionByIsin.get(sellTransaction.getIsin());

        if(buyTransaction.getQuantity().getValue() != sellTransaction.getQuantity().getValue()) {
            throw new DomainException("Partial sell transactions are not supported.");
        }

        this.lastOpenBuyTransactionByIsin.remove(sellTransaction.getIsin());

        Amount profitAmount =
                sellTransaction.getTotalPrice()
                .subtract(sellTransaction.getCommission())
                .subtract(buyTransaction.getTotalPrice())
                .subtract(buyTransaction.getCommission());

        return new Profit(ProfitCategories.Sale, profitAmount);
    }

    private Profit registerDividendTransactionAndCalculateTransactionProfit(DividendTransaction dividendTransaction) {
        Amount profitAmount = dividendTransaction.getAmount();
        return new Profit(ProfitCategories.Dividends, profitAmount);
    }

    private Profit registerTaxPaymentTransactionAndCalculateTransactionProfit(TaxPaymentTransaction taxPaymentTransaction) {
        return null;
    }
}
