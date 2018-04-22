package trading.api.account;

import trading.domain.account.Position;

public class AccountPositionDto {
    public String isin;
    public int quantity;
    public double fullMarketPrice;

    public AccountPositionDto() {

    }

    public AccountPositionDto(Position position) {
        this.isin = position.getISIN().getText();
        this.quantity = position.getQuantity().getValue();
        this.fullMarketPrice = position.getFullMarketPrice().getValue();
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getFullMarketPrice() {
        return fullMarketPrice;
    }

    public void setFullMarketPrice(double fullMarketPrice) {
        this.fullMarketPrice = fullMarketPrice;
    }
}
