package trading.api.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import trading.api.ClientException;
import trading.application.AccountService;
import trading.domain.*;
import trading.domain.account.Account;
import trading.domain.account.AccountId;
import trading.domain.account.Position;
import trading.domain.market.InstrumentNameProvider;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.simulation.MultiStockMarketDataStore;

import java.time.LocalDate;
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
    private AccountId accountId = new AccountId(1);

    private Account getAccount() {
        return this.accountService.getAccount(this.accountId);
    }

    @RequestMapping(value = "/api/account/positions/", method = RequestMethod.GET)
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

        response.setMarketPricesDate(lastClosingPrices.getDate());

        return response;
    }

    @RequestMapping(value = "/api/account/positions/{isin}", method = RequestMethod.GET)
    public GetAccountPositionResponse getAccountPosition(@PathVariable("isin") String isin) {
        return new GetAccountPositionResponse();
    }

    @RequestMapping(value = "/api/account/transactions/", method = RequestMethod.GET)
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

    @RequestMapping(value = "/api/account/transactions/{transactionId}", method = RequestMethod.GET)
    public GetAccountTransactionResponse getAccountTransaction(@PathVariable("transactionId") Integer transactionId) {
        return new GetAccountTransactionResponse();
    }

    @RequestMapping(value = "/api/account/transactions/", method = RequestMethod.POST)
    public RegisterTransactionResponse registerTransaction(@RequestBody RegisterTransactionRequest request) {
        try {
            TransactionBuilder transactionBuilder = new TransactionBuilder();

            if(request.getDate() != null) {
                transactionBuilder.setDate(request.getDate());
            }

            if (request.getTransactionType() != null) {
                if(request.getTransactionType().isEmpty()) {
                    throw new ClientException("The transaction type must not be empty.");
                }

                TransactionType transactionType;

                try {
                    transactionType = TransactionType.valueOf(request.getTransactionType());
                }
                catch(IllegalArgumentException illegalArgumentException) {
                    throw new ClientException(String.format("The transaction type '%s' is invalid.", request.getTransactionType()));
                }

                transactionBuilder.setTransactionType(transactionType);
            }

            if(request.getIsin() != null && !request.getIsin().isEmpty()) {
                ISIN isin = new ISIN(request.getIsin());
                transactionBuilder.setIsin(isin);
            }

            if(request.getQuantity() != null) {
                Quantity quantity = new Quantity(request.getQuantity());
                transactionBuilder.setQuantity(quantity);
            }

            if(request.getTotalPrice() != null) {
                Amount totalPrice = new Amount(request.getTotalPrice());
                transactionBuilder.setTotalPrice(totalPrice);
            }

            if(request.getCommission() != null) {
                Amount commission = new Amount(request.getCommission());
                transactionBuilder.setCommission(commission);
            }

            Transaction transaction = transactionBuilder.build();

            if(this.instrumentNameProvider.getInstrumentName(transaction.getIsin()) == null) {
                throw new ClientException("The given ISIN is unknown.");
            }

            this.accountService.registerTransaction(this.accountId, transaction);

            RegisterTransactionResponse response = new RegisterTransactionResponse();
            response.setTransactionId(transaction.getId().getValue());
            return response;
        }
        catch(DomainException domainException) {
            throw new ClientException(domainException);
        }
    }
}
