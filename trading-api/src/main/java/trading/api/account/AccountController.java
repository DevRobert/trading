package trading.api.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trading.application.AccountService;
import trading.domain.ISIN;
import trading.domain.Transaction;
import trading.domain.account.Account;
import trading.domain.account.AccountId;
import trading.domain.account.Position;
import trading.domain.market.InstrumentNameProvider;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.simulation.MultiStockMarketDataStore;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:3001")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private InstrumentNameProvider instrumentNameProvider;

    @Autowired
    private MultiStockMarketDataStore multiStockMarketDataStore;

    // TODO get account id from JWT
    private AccountId accountId = new AccountId(2);

    private Account getAccount() {
        return this.accountService.getAccount(this.accountId);
    }

    @RequestMapping("/api/account/positions/")
    public GetAccountPositionsResponse getAccountPositions() {
        GetAccountPositionsResponse response = new GetAccountPositionsResponse();

        Account account = this.getAccount();
        List<AccountPositionDto> accountPositionDtos = new ArrayList<>();

        MarketPriceSnapshot lastClosingPrices = this.multiStockMarketDataStore.getLastClosingPrices();
        account.reportMarketPrices(lastClosingPrices);

        for(ISIN isin: account.getCurrentStocks().keySet()) {
            Position position = account.getPosition(isin);

            AccountPositionDto accountPositionDto = new AccountPositionDto();
            accountPositionDto.setIsin(isin.getText());
            accountPositionDto.setQuantity(position.getQuantity().getValue());
            accountPositionDto.setTotalMarketPrice(position.getFullMarketPrice().getValue());
            accountPositionDto.setMarketPrice(lastClosingPrices.getMarketPrice(isin).getValue());

            String instrumentName = this.instrumentNameProvider.getInstrumentName(isin);

            if(instrumentName == null) {
                instrumentName = "Unknown";
            }

            accountPositionDto.setName(instrumentName);

            accountPositionDtos.add(accountPositionDto);
        }

        response.setPositions(accountPositionDtos);

        AccountSummaryDto accountSummaryDto = new AccountSummaryDto();
        accountSummaryDto.setAvailableMoney(account.getAvailableMoney().getValue());
        accountSummaryDto.setTotalBalance(account.getBalance().getValue());
        accountSummaryDto.setTotalStocksMarketPrice(account.getTotalStocksMarketPrice().getValue());
        accountSummaryDto.setTotalStocksQuantity(account.getTotalStocksQuantity().getValue());

        response.setSummary(accountSummaryDto);

        return response;
    }

    @RequestMapping("/api/account/positions/{isin}")
    public GetAccountPositionResponse getAccountPosition(@PathVariable("isin") String isin) {
        return new GetAccountPositionResponse();
    }

    @RequestMapping("/api/account/transactions/")
    public GetAccountTransactionsResponse getAccountTransactions() {
        GetAccountTransactionsResponse response = new GetAccountTransactionsResponse();

        Account account = this.getAccount();

        List<AccountTransactionDto> accountTransactionDtos = new ArrayList<>();

        for(Transaction transaction: account.getProcessedTransactions()) {
            AccountTransactionDto accountTransactionDto = new AccountTransactionDto();
            accountTransactionDto.setDate(transaction.getDate());
            accountTransactionDto.setTransactionType(transaction.getTransactionType().toString());
            accountTransactionDto.setIsin(transaction.getIsin().getText());
            accountTransactionDto.setQuantity(transaction.getQuantity().getValue());

            double marketPrice = transaction.getTotalPrice().getValue() / transaction.getQuantity().getValue();
            accountTransactionDto.setMarketPrice(marketPrice);

            accountTransactionDto.setTotalPrice(transaction.getTotalPrice().getValue());
            accountTransactionDto.setCommission(transaction.getCommission().getValue());

            String instrumentName = this.instrumentNameProvider.getInstrumentName(transaction.getIsin());

            if(instrumentName == null) {
                instrumentName = "Unknown";
            }

            accountTransactionDto.setName(instrumentName);

            accountTransactionDtos.add(accountTransactionDto);
        }

        response.setTransactions(accountTransactionDtos);

        return response;
    }

    @RequestMapping("/api/account/transactions/{transactionId}")
    public GetAccountTransactionResponse getAccountTransaction(@PathVariable("transactionId") Integer transactionId) {
        return new GetAccountTransactionResponse();
    }
}
