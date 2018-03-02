package trading;

public class Position {
    private ISIN isin;
    private Quantity quantity;
    private Amount fullMarketPrice;
    private boolean creationPending;

    public ISIN getISIN() {
        return isin;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    protected void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public Amount getFullMarketPrice() {
        return fullMarketPrice;
    }

    protected void setFullMarketPrice(Amount fullMarketPrice) {
        this.fullMarketPrice = fullMarketPrice;
    }

    public boolean isCreationPending() {
        return this.creationPending;
    }

    public Position(ISIN isin, Quantity quantity, Amount fullMarketPrice) {
        this.isin = isin;
        this.quantity = quantity;
        this.fullMarketPrice = fullMarketPrice;
        this.creationPending = true;
    }

    public void confirmCreation() {
        this.creationPending = false;
    }
}
