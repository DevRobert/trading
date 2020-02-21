package trading.api.account;

import java.time.LocalDate;

public class RegisterTransactionRequest {
    private LocalDate date;
    private String transactionType;
    private String isin;
    private Integer quantity;
    private Double totalPrice;
    private Double commission;
    private Double amount;
    private String profitCategory;
    private Integer taxPeriodYear;
    private Double taxedProfit;
    private Double paidTaxes;

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

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public String getProfitCategory() {
        return profitCategory;
    }

    public void setProfitCategory(String profitCategory) {
        this.profitCategory = profitCategory;
    }

    public Integer getTaxPeriodYear() {
        return taxPeriodYear;
    }

    public void setTaxPeriodYear(Integer taxPeriodYear) {
        this.taxPeriodYear = taxPeriodYear;
    }

    public Double getTaxedProfit() {
        return taxedProfit;
    }

    public void setTaxedProfit(Double taxedProfit) {
        this.taxedProfit = taxedProfit;
    }

    public Double getPaidTaxes() {
        return paidTaxes;
    }

    public void setPaidTaxes(Double paidTaxes) {
        this.paidTaxes = paidTaxes;
    }
}
