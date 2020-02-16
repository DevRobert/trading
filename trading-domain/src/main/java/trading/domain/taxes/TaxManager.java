package trading.domain.taxes;

import trading.domain.Amount;
import trading.domain.account.TaxPaymentTransaction;
import trading.domain.account.TaxStrategy;
import trading.domain.account.Transaction;

public class TaxManager {
    private final TaxStrategy taxStrategy;
    private final ProfitTaxation saleProfitTaxation;
    private final ProfitTaxation dividendProfitTaxation;
    private final ProfitCalculator profitCalculator = new ProfitCalculator();

    public TaxManager(TaxStrategy taxStrategy) {
        this.taxStrategy = taxStrategy;

        this.saleProfitTaxation = new ProfitTaxation(taxStrategy.getSaleTaxCalculator());
        this.dividendProfitTaxation = new ProfitTaxation(taxStrategy.getDividendTaxCalculator());
    }

    public TaxStrategy getTaxStrategy() {
        return this.taxStrategy;
    }

    public TaxImpact registerTransactionAndCalculateTaxImpact(Transaction transaction) {
        Amount reservedTaxesBefore = this.getReservedTaxes();
        Amount paidTaxesBefore = this.getPaidTaxes();

        this.handleTransactionProfit(transaction);
        this.handleTransactionTaxPayment(transaction);

        Amount addedReservedTaxes = this.getReservedTaxes().subtract(reservedTaxesBefore);
        Amount addedPaidTaxes = this.getPaidTaxes().subtract(paidTaxesBefore);

        return new TaxImpact(addedReservedTaxes, addedPaidTaxes);
    }

    private void handleTransactionProfit(Transaction transaction) {
        Profit profit = this.profitCalculator.registerTransactionAndCalculateTransactionProfit(transaction);

        if(profit == null) {
            return;
        }

        if(profit.getProfitCategory() == ProfitCategories.Sale) {
            this.saleProfitTaxation.registerProfit(profit.getAmount());
        }
        else if(profit.getProfitCategory() == ProfitCategories.Dividends) {
            this.dividendProfitTaxation.registerProfit(profit.getAmount());
        }
        else {
            throw new RuntimeException("Unknown profit category.");
        }
    }

    private void handleTransactionTaxPayment(Transaction transaction) {
        if(transaction instanceof TaxPaymentTransaction == false) {
            return;
        }

        TaxPaymentTransaction taxPaymentTransaction = (TaxPaymentTransaction) transaction;

        if(taxPaymentTransaction.getProfitCategory() == ProfitCategories.Sale) {
            this.saleProfitTaxation.registerTaxPayment(
                    taxPaymentTransaction.getTaxedProfit(),
                    taxPaymentTransaction.getPaidTaxes()
            );
        }
        else if(taxPaymentTransaction.getProfitCategory() == ProfitCategories.Dividends) {
            this.dividendProfitTaxation.registerTaxPayment(
                    taxPaymentTransaction.getTaxedProfit(),
                    taxPaymentTransaction.getPaidTaxes()
            );
        }
        else {
            throw new RuntimeException("Unknown profit category.");
        }
    }

    public Amount getPaidTaxes() {
        return this.saleProfitTaxation.getPaidTaxes()
                .add(this.dividendProfitTaxation.getPaidTaxes());
    }

    public Amount getReservedTaxes() {
        return this.saleProfitTaxation.getReservedTaxes()
                .add(this.dividendProfitTaxation.getReservedTaxes());
    }
}
