package trading.domain.account;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.DomainException;

public class AccountBuilderTest {
    private AccountBuilder accountBuilder;

    @Before
    public void before() {
        this.accountBuilder = new AccountBuilder()
                .setAvailableMoney(new Amount(10000.0))
                .setTaxStrategy(TaxStrategies.getNoTaxesStrategy());
    }

    @Test
    public void createAccount() {
        Account account = this.accountBuilder.build();

        Assert.assertEquals(new Amount(10000.0), account.getAvailableMoney());
        Assert.assertSame(TaxStrategies.getNoTaxesStrategy(), account.getTaxStrategy());
    }

    @Test
    public void createAccountFails_ifAvailableMoneyNotSet() {
        this.accountBuilder.setAvailableMoney(null);

        try {
            this.accountBuilder.build();
        }
        catch(DomainException e) {
            Assert.assertEquals("The available money must be set.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }

    @Test
    public void createAccountFails_ifTaxStrategyNotSet() {
        this.accountBuilder.setTaxStrategy(null);

        try {
            this.accountBuilder.build();
        }
        catch(DomainException e) {
            Assert.assertEquals("The tax strategy must be set.", e.getMessage());
            return;
        }

        Assert.fail("DomainException expected.");
    }
}
