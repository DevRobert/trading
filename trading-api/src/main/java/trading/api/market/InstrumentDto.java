package trading.api.market;

public class InstrumentDto {
    private String isin;
    private String name;
    private Double marketPrice;

    public String getIsin() {
        return this.isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMarketPrice() {
        return this.marketPrice;
    }

    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }
}
