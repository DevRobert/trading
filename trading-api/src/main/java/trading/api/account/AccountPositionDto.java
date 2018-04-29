package trading.api.account;

import trading.domain.account.Position;

public class AccountPositionDto {
    private String isin;
    private String name;
    private int quantity;
    private double marketPrice;
    private double totalMarketPrice;

    public AccountPositionDto() {

    }

    public AccountPositionDto(Position position) {
        this.isin = position.getISIN().getText();
        this.quantity = position.getQuantity().getValue();
        this.totalMarketPrice = position.getFullMarketPrice().getValue();
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getMarketPrice() {
        return this.marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public double getTotalMarketPrice() {
        return totalMarketPrice;
    }

    public void setTotalMarketPrice(double totalMarketPrice) {
        this.totalMarketPrice = totalMarketPrice;
    }
}
