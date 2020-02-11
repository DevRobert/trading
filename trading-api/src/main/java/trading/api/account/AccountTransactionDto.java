package trading.api.account;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class AccountTransactionDto {
    private LocalDate date;
    private String transactionType;
    private String isin;
    private String name;
    private Integer quantity;
    private Double marketPrice;
    private Double totalPrice;
    private Double commission;
    private Double amount;
    private Double taxImpact;

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getMarketPrice() {
        return this.marketPrice;
    }

    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Double getTotalPrice() {
        return this.totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getCommission() {
        return this.commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public Double getAmount() {
        return this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTaxImpact() {
        return this.taxImpact;
    }

    public void setTaxImpact(Double taxImpact) {
        this.taxImpact = taxImpact;
    }
}
