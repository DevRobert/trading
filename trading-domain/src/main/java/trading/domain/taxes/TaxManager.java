package trading.domain.taxes;

import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.account.TaxPaymentTransaction;
import trading.domain.account.TaxStrategy;
import trading.domain.account.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TaxManager {
    private final TaxStrategy taxStrategy;
    private final ProfitCalculator profitCalculator = new ProfitCalculator();
    private final List<TaxPeriod> taxPeriods = new ArrayList<>();

    public TaxManager(TaxStrategy taxStrategy) {
        this.taxStrategy = taxStrategy;
    }

    public TaxStrategy getTaxStrategy() {
        return this.taxStrategy;
    }

    private TaxPeriod getExistingTaxPeriod(int year) {
        for(int taxPeriodIndex = this.taxPeriods.size() - 1; taxPeriodIndex >= 0; taxPeriodIndex--) {
            TaxPeriod taxPeriod = this.taxPeriods.get(taxPeriodIndex);

            if(taxPeriod.getYear() == year) {
                return taxPeriod;
            }
        }

        return null;
    }

    private TaxPeriod getOrCreateTaxPeriod(int year) {
        TaxPeriod existingTaxPeriod = this.getExistingTaxPeriod(year);

        if(existingTaxPeriod != null) {
            return existingTaxPeriod;
        }

        TaxPeriod previousTaxPeriod = this.getExistingTaxPeriod(year - 1);

        if(this.taxPeriods.size() > 0 && this.taxPeriods.get(this.taxPeriods.size() - 1).getYear() > year) {
            System.out.println(this.taxPeriods.get(this.taxPeriods.size() - 1).getYear() + " > " + year);
            throw new DomainException("The tax period cannot be added because there already exists a tax period for a future year.");
        }

        TaxPeriod newTaxPeriod = new TaxPeriod(year, previousTaxPeriod, this.taxStrategy);
        this.taxPeriods.add(newTaxPeriod);
        return newTaxPeriod;
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

        TaxPeriod taxPeriod = this.getOrCreateTaxPeriod(transaction.getDate().getYear());
        taxPeriod.registerProfit(profit);
    }

    private void handleTransactionTaxPayment(Transaction transaction) {
        if(transaction instanceof TaxPaymentTransaction == false) {
            return;
        }

        TaxPaymentTransaction taxPaymentTransaction = (TaxPaymentTransaction) transaction;

        TaxPeriod taxPeriod = this.getOrCreateTaxPeriod(taxPaymentTransaction.getTaxPeriodYear());

        taxPeriod.registerTaxPayment(
                taxPaymentTransaction.getProfitCategory(),
                taxPaymentTransaction.getTaxedProfit(),
                taxPaymentTransaction.getPaidTaxes()
        );
    }

    public Amount getReservedTaxes() {
        Amount reservedTaxes = Amount.Zero;

        for(TaxPeriod taxPeriod: this.taxPeriods) {
            reservedTaxes = reservedTaxes.add(taxPeriod.getReservedTaxes());
        }

        return reservedTaxes;
    }

    public Amount getPaidTaxes() {
        Amount paidTaxes = Amount.Zero;

        for(TaxPeriod taxPeriod: this.taxPeriods) {
            paidTaxes = paidTaxes.add(taxPeriod.getPaidTaxes());
        }

        return paidTaxes;
    }
}
