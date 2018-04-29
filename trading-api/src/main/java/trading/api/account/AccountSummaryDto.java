package trading.api.account;

public class AccountSummaryDto {
    private int totalStocksQuantity;
    private double totalStocksMarketPrice;
    private double availableMoney;
    private double totalBalance;

    public int getTotalStocksQuantity() {
        return totalStocksQuantity;
    }

    public void setTotalStocksQuantity(int totalStocksQuantity) {
        this.totalStocksQuantity = totalStocksQuantity;
    }

    public double getTotalStocksMarketPrice() {
        return this.totalStocksMarketPrice;
    }

    public void setTotalStocksMarketPrice(double totalStocksPrice) {
        this.totalStocksMarketPrice = totalStocksPrice;
    }

    public double getAvailableMoney() {
        return this.availableMoney;
    }

    public void setAvailableMoney(double availableMoney) {
        this.availableMoney = availableMoney;
    }

    public double getTotalBalance() {
        return this.totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }
}
