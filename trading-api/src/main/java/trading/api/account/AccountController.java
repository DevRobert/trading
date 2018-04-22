package trading.api.account;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
    @RequestMapping("/api/accounts/{accountId}/positions/")
    public GetAccountPositionsResponse getAccountPositions(@PathVariable("accountId") Integer accountId) {
        return new GetAccountPositionsResponse();
    }

    @RequestMapping("/api/accounts/{accountId}/positions/{isin}")
    public GetAccountPositionResponse getAccountPosition(@PathVariable("accountId") Integer accountId, @PathVariable("isin") String isin) {
        return new GetAccountPositionResponse();
    }

    @RequestMapping("/api/accounts/{accountId}/transactions/")
    public GetAccountTransactionsResponse getAccountTransactions(@PathVariable("accountId") Integer accountId) {
        return new GetAccountTransactionsResponse();
    }

    @RequestMapping("/api/accounts/{accountId}/transactions/{transactionId}")
    public GetAccountTransactionResponse getAccountTransaction(@PathVariable("accountId") Integer accountId, @PathVariable("transactionId") Integer transactionId) {
        return new GetAccountTransactionResponse();
    }
}
