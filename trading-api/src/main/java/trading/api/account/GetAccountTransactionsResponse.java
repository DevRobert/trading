package trading.api.account;

import java.util.List;

public class GetAccountTransactionsResponse {
    private List<AccountTransactionDto> transactions;

    public List<AccountTransactionDto> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<AccountTransactionDto> transactions) {
        this.transactions = transactions;
    }
}
