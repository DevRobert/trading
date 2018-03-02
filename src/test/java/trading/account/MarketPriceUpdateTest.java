package trading.account;

import org.junit.Assert;
import org.junit.Test;
import trading.Amount;
import trading.ISIN;

public class MarketPriceUpdateTest extends AccountTestBase {
    @Test
    public void increasingMarketPriceIncreasesPositionTotalMarketPrice() throws AccountStateException {
        // Seeding money 10,000
        // Price: 5,000 for 10 shares (500 per share)
        // Commission: 10

        this.prepareAccountWithBuyTransaction();

        account.reportMarketPrice(ISIN.MunichRe, new Amount(600.0));

        // New price: 6,000 for 10 shares (600 per share)

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(new Amount(6000.0), position.getFullMarketPrice());
    }

    @Test
    public void decreasingMarketPriceDecreasesPositionTotalMarketPrice() throws AccountStateException {
        // Seeding money 10,000
        // Price: 5,000 for 10 shares (500 per share)
        // Commission: 10

        this.prepareAccountWithBuyTransaction();

        account.reportMarketPrice(ISIN.MunichRe, new Amount(400.0));

        // New price: 4,000 for 10 shares (400 per share)

        Position position = account.getPosition(ISIN.MunichRe);
        Assert.assertEquals(new Amount(4000.0), position.getFullMarketPrice());
    }

    @Test
    public void increasingMarketPriceIncreasesAccountBalance() throws AccountStateException {
        // Seeding money 10,000
        // Price: 5,000 for 10 shares (500 per share)
        // Commission: 10

        this.prepareAccountWithBuyTransaction();

        // Intermediate balance: 9,990

        account.reportMarketPrice(ISIN.MunichRe, new Amount(600.0));

        // New price: 6,000 for 10 shares (600 per share)
        // New balance: 9,990 + 1,000 = 10,990

        Assert.assertEquals(new Amount(10990.0), account.getBalance());
    }

    @Test
    public void decreasingMarketPriceDecreasesAccountBalance() throws AccountStateException {
        // Seeding money 10,000
        // Price: 5,000 for 10 shares (500 per share)
        // Commission: 10

        this.prepareAccountWithBuyTransaction();

        // Intermediate balance: 9,990

        account.reportMarketPrice(ISIN.MunichRe, new Amount(400.0));

        // New price: 4,000 for 10 shares (400 per share)
        // New balance: 9,990 - 1,000 = 8,990

        Assert.assertEquals(new Amount(8990.0), account.getBalance());
    }

    @Test
    public void increasingMarketPriceDoesNotAffectAvailableMoney() throws AccountStateException {
        // Seeding money 10,000
        // Price: 5,000 for 10 shares (500 per share)
        // Commission: 10

        this.prepareAccountWithBuyTransaction();

        // Available money: 4,990
        // Intermediate balance: 9,990

        account.reportMarketPrice(ISIN.MunichRe, new Amount(600.0));

        // New price: 6,000 for 10 shares (600 per share)
        // New balance: 9,990 + 1,000 = 10,990
        // Available money: still 4,990

        Assert.assertEquals(new Amount(4990.0), account.getAvailableMoney());
    }
}
