package trading.account;

import org.junit.Before;
import trading.*;

public abstract class AccountTestBase {
    protected Account account;

    @Before()
    public void before() {
        Amount availableMoney = new Amount(10000.0);
        this.account = new Account(availableMoney);
    }

    protected void prepareAccountWithBuyTransaction() throws AccountStateException {
        // Initial available money = 10,000
        // Initial balance = 10,000

        Amount fullPrice = new Amount(5000.0);
        Amount commission = new Amount(10.0);

        Transaction transaction = new Transaction(TransactionType.Buy, ISIN.MunichRe, new Quantity(10), fullPrice, commission);
        account.registerTransaction(transaction);

        // New available money = 10,000 - 5,000 - 10 = 4,990
        // New balance = 10,000 - 10 = 9,990
    }
}
