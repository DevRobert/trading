package trading.domain.account;

import org.junit.Assert;
import org.junit.Test;

public class AccountIdTest {
    @Test
    public void returnsValue() {
        AccountId accountId = new AccountId(10);
        Assert.assertEquals(10, accountId.getValue());
    }

    @Test
    public void toStringReturnsValue() {
        AccountId accountId = new AccountId(10);
        Assert.assertEquals("10", accountId.toString());
    }
}