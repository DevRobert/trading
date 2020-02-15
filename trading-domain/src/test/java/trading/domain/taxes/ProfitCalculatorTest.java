package trading.domain.taxes;

import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.Quantity;
import trading.domain.account.MarketTransactionBuilder;
import trading.domain.account.Transaction;
import trading.domain.account.TransactionType;

import java.time.LocalDate;

public class ProfitCalculatorTest {
    private ProfitCalculator profitCalculator;

    @Before
    public void initializeProfitCalculator() {
        this.profitCalculator = new ProfitCalculator();
    }

    @Test
    public void doesNotCalculateAnyProfitForBuy() {

    }

    @Test
    public void calculatesProfitForSale() {
        Transaction buyTransaction = new MarketTransactionBuilder()
            .setTransactionType(TransactionType.Buy)
            .setIsin(ISIN.MunichRe)
            .setQuantity(new Quantity(1))
            .setTotalPrice(new Amount(1000.0))
            .setCommission(new Amount(10.0))
            .setDate(LocalDate.of(2000, 1, 1))
            .build();

        Transaction sellTransaction = new MarketTransactionBuilder()
            .setTransactionType(TransactionType.Sell)
            .setIsin(ISIN.MunichRe)
            .setQuantity(new Quantity(1))
            .setTotalPrice(new Amount(2000.0))
            .setCommission(new Amount(20.0))
            .setDate(LocalDate.of(2000, 1, 2))
            .build();
    }

    @Test
    public void calculatesProfitForDividend() {

    }
}
