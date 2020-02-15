package trading.domain.account;

import trading.domain.Amount;
import trading.domain.DomainException;
import trading.domain.ISIN;
import trading.domain.Quantity;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.taxes.TaxCalculator;
import trading.domain.taxes.TaxConfiguration;
import trading.domain.taxes.TaxManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Account {
    private AccountId id;
    private Amount availableMoney;
    private HashMap<ISIN, Position> positions = new HashMap<>();
    private Amount commissions;
    private Amount balance;

    private final List<Transaction> processedTransactions;
    private final Map<ISIN, MarketTransaction> lastMarketTransactionByIsin;
    private final Map<ISIN, MarketTransaction> lastBuyTransactionByIsin;

    private final TaxManager taxManager;

    Account(Amount availableMoney, TaxStrategy taxStrategy) {
        if(availableMoney == null) {
            throw new DomainException("The available money must be set.");
        }

        if(taxStrategy == null) {
            throw new DomainException("The tax strategy must be set.");
        }

        this.taxManager = new TaxManager(taxStrategy);

        this.commissions = Amount.Zero;
        this.availableMoney = availableMoney;
        this.balance = availableMoney;
        this.processedTransactions = new ArrayList<>();
        this.lastMarketTransactionByIsin = new HashMap<>();
        this.lastBuyTransactionByIsin = new HashMap<>();
    }

    public TaxStrategy getTaxStrategy() {
        return this.taxManager.getTaxStrategy();
    }

    public Amount getCommissions() {
        return commissions;
    }

    public Amount getAvailableMoney() {
        return availableMoney;
    }

    public Amount getBalance() {
        return balance;
    }

    public Position getPosition(ISIN isin) throws PositionNotFoundException {
        Position position = this.positions.get(isin);

        if(position == null) {
            throw new PositionNotFoundException();
        }

        return position;
    }

    public boolean hasPosition(ISIN isin) {
        return this.positions.containsKey(isin);
    }

    public List<Transaction> getProcessedTransactions() {
        return processedTransactions;
    }

    public AccountId getId() {
        return this.id;
    }

    public void setId(AccountId accountId) {
        if(this.id != null) {
            throw new DomainException("The account id must not be changed if set once.");
        }

        this.id = accountId;
    }

    public void registerTransaction(Transaction transaction) {
        if(this.processedTransactions.size() > 0) {
            Transaction lastProcessedTransaction = this.processedTransactions.get(this.processedTransactions.size() - 1);

            if(transaction.getDate().isBefore(lastProcessedTransaction.getDate())) {
                throw new DomainException(String.format(
                        "The transaction cannot be registered as its date (%s) lies before the date of the last registered transaction (%s).",
                        transaction.getDate().toString(),
                        lastProcessedTransaction.getDate().toString()));
            }
        }

        if(transaction instanceof MarketTransaction) {
            this.registerMarketTransaction((MarketTransaction) transaction);
        }
        else if(transaction instanceof DividendTransaction) {
            this.registerDividendTransaction((DividendTransaction) transaction);
        }
        else if(transaction instanceof TaxPaymentTransaction) {
            // nothing to do here; effects take place in tax impact calculation
        }
        else {
            throw new RuntimeException("Unknown transaction type.");
        }

        this.processedTransactions.add(transaction);

        this.calculateAndRegisterTransactionTaxImpact(transaction);
    }

    private void registerMarketTransaction(MarketTransaction transaction) throws AccountStateException {
        this.ensureTransactionCanBePaid(transaction);

        Position position = this.getPositionOrCreatePending(transaction.getIsin());

        try {
            this.handleTransaction(transaction, position);
        }
        catch(AccountStateException ex) {
            if(position.isCreationPending()) {
                this.positions.remove(transaction.getIsin());
            }

            throw ex;
        }

        if(position.isCreationPending()) {
            position.confirmCreation();
        }

        this.updateBalances(transaction);

        this.lastMarketTransactionByIsin.put(transaction.getIsin(), transaction);

        if(transaction.getTransactionType() == TransactionType.Buy) {
            this.lastBuyTransactionByIsin.put(transaction.getIsin(), transaction);
        }
    }

    private void registerDividendTransaction(DividendTransaction transaction) {
        this.ensureDividendTransactionRelatesToRespectivePosition(transaction);

        this.availableMoney = this.availableMoney.add(transaction.getAmount());
        this.balance = this.balance.add(transaction.getAmount());
    }

    private void ensureDividendTransactionRelatesToRespectivePosition(DividendTransaction transaction) {
        if(!this.hasPosition(transaction.getIsin())) {
            throw new DomainException("The dividend transaction cannot be registered as there was no respective position found for the given ISIN.");
        }

        Position position = this .getPosition(transaction.getIsin());

        if(position.getQuantity().isZero()) {
            MarketTransaction sellTransaction = this.getLastMarketTransaction(transaction.getIsin());

            if(sellTransaction.getTransactionType() != MarketTransactionType.Sell) {
                throw new RuntimeException("Sell transaction expected.");
            }

            if (transaction.getDate().minusDays(30).isAfter(sellTransaction.getDate())) {
                throw new DomainException(String.format(
                        "The dividend transaction cannot be registered as the dividend date (%s) lies more " +
                        "than 30 days after the close date of the respective position (%s).",
                        transaction.getDate().toString(),
                        sellTransaction.getDate().toString()));
            }
        }
    }

    private Position getPositionOrCreatePending(ISIN isin) {
        if(this.hasPosition(isin)) {
            return this.getPosition(isin);
        }

        Position position = new Position(isin, Quantity.Zero, Amount.Zero);
        this.positions.put(isin, position);
        return position;
    }

    private void ensureTransactionCanBePaid(MarketTransaction transaction) throws AccountStateException {
        Amount requiredAmount;

        if(transaction.getTransactionType() == MarketTransactionType.Buy) {
            requiredAmount = transaction.getTotalPrice();
        }
        else if(transaction.getTransactionType() == MarketTransactionType.Sell) {
            requiredAmount = Amount.Zero;
        }
        else {
            throw new RuntimeException("MarketTransaction type not supported: " + transaction.getTransactionType());
        }

        if(requiredAmount.getValue() > 0 && requiredAmount.getValue() > this.availableMoney.getValue()) {
            throw new AccountStateException("The total price exceeds the available money.");
        }
    }

    private void updateBalances(MarketTransaction transaction) {
        this.commissions = this.commissions.add(transaction.getCommission());

        if(transaction.getTransactionType() == MarketTransactionType.Buy) {
            this.availableMoney = this.availableMoney.subtract(transaction.getTotalPrice());
        }
        else if(transaction.getTransactionType() == MarketTransactionType.Sell) {
            this.availableMoney = this.availableMoney.add(transaction.getTotalPrice());
        }
        else {
            throw new RuntimeException("MarketTransaction type not supported: " + transaction.getTransactionType());
        }

        this.availableMoney = this.availableMoney.subtract(transaction.getCommission());

        this.balance = this.balance.subtract(transaction.getCommission());
    }

    private void handleTransaction(MarketTransaction transaction, Position position) throws AccountStateException {
        if(transaction.getTransactionType() == MarketTransactionType.Buy) {
            this.handleBuyTransaction(transaction, position);
        }
        else if(transaction.getTransactionType() == MarketTransactionType.Sell) {
            this.handleSellTransaction(transaction, position);
        }
        else {
            throw new RuntimeException("MarketTransaction type not supported: " + transaction.getTransactionType());
        }
    }

    private void handleBuyTransaction(MarketTransaction transaction, Position position) throws AccountStateException {
        if(!position.getQuantity().isZero()) {
            throw new AccountStateException("Subsequent buy transactions for non-empty positions are not supported.");
        }

        position.setQuantity(transaction.getQuantity());
        position.setFullMarketPrice(transaction.getTotalPrice());
    }

    private void handleSellTransaction(MarketTransaction transaction, Position position) throws AccountStateException {
        if(position.isCreationPending()) {
            throw new AccountStateException("The sell transaction could not be processed because there was no respective position found.");
        }

        this.preventPartialSellTransactions(transaction, position);
        this.preventExceedingSellTransactions(transaction, position);

        Amount margin = transaction.getTotalPrice().subtract(position.getFullMarketPrice());
        this.balance = this.balance.add(margin);

        Quantity quantity = position.getQuantity().subtract(transaction.getQuantity());
        position.setQuantity(quantity);
        position.setFullMarketPrice(Amount.Zero);
    }

    private void calculateAndRegisterTransactionTaxImpact(Transaction transaction) {
        Amount paidTaxesBefore = this.getPaidTaxes();
        Amount reservedTaxesBefore = this.getReservedTaxes();

        this.taxManager.registerTransaction(transaction);

        Amount paidTaxesAdded = this.getPaidTaxes().subtract(paidTaxesBefore);
        Amount reservedTaxesAdded = this.getReservedTaxes().subtract(reservedTaxesBefore);

        this.availableMoney = this.availableMoney.subtract(paidTaxesAdded);
        this.availableMoney = this.availableMoney.subtract(reservedTaxesAdded);

        this.balance = this.balance.subtract(paidTaxesAdded);
        this.balance = this.balance.subtract(reservedTaxesAdded);
    }

    private void preventPartialSellTransactions(MarketTransaction transaction, Position position) throws AccountStateException {
        if(transaction.getQuantity().getValue() < position.getQuantity().getValue()) {
            throw new AccountStateException("Partial sell transactions are not supported.");
        }
    }

    private void preventExceedingSellTransactions(MarketTransaction transaction, Position position) throws AccountStateException {
        if(transaction.getQuantity().getValue() > position.getQuantity().getValue()) {
            throw new AccountStateException("The sell transaction states a higher quantity than the position has.");
        }
    }

    public void reportMarketPrices(MarketPriceSnapshot marketPriceSnapshot) {
        for(Position position: this.positions.values()) {
            Amount marketPrice = marketPriceSnapshot.getMarketPrice(position.getISIN());
            Amount previousFullMarketPrice = position.getFullMarketPrice();
            Amount newFullMarketPrice = marketPrice.multiply(position.getQuantity());
            Amount delta = newFullMarketPrice.subtract(previousFullMarketPrice);
            position.setFullMarketPrice(newFullMarketPrice);
            this.balance = this.balance.add(delta);
        }
    }

    public Map<ISIN, Quantity> getCurrentStocks() {
        Map<ISIN, Quantity> currentStocks = new HashMap<>();

        for(ISIN isin: this.positions.keySet()) {
            Quantity quantity = this.positions.get(isin).getQuantity();

            if(!quantity.isZero()) {
                currentStocks.put(isin, quantity);
            }
        }

        return currentStocks;
    }

    public Amount getTotalStocksMarketPrice() {
        Amount totalStocksMarketPrice = Amount.Zero;

        for(Position position: this.positions.values()) {
            totalStocksMarketPrice = totalStocksMarketPrice.add(position.getFullMarketPrice());
        }

        return totalStocksMarketPrice;
    }

    public Quantity getTotalStocksQuantity() {
        Quantity totalStocksQuantity = Quantity.Zero;

        for(Position position: this.positions.values()) {
            totalStocksQuantity = new Quantity(totalStocksQuantity.getValue() + position.getQuantity().getValue());
        }

        return totalStocksQuantity;
    }

    public MarketTransaction getLastMarketTransaction(ISIN isin) {
        MarketTransaction transaction = this.lastMarketTransactionByIsin.get(isin);

        if(transaction == null) {
            throw new RuntimeException("There was no transaction registered yet for the specified ISIN.");
        }

        return transaction;
    }

    public MarketTransaction findLastBuyTransaction(ISIN isin) {
        MarketTransaction buyTransaction = this.lastBuyTransactionByIsin.get(isin);

        if(buyTransaction == null) {
            throw new DomainException("There was no buy transaction registered for the given ISIN.");
        }

        return buyTransaction;
    }

    public Amount getReservedTaxes() {
        return this.taxManager.getReservedTaxes();
    }

    public Amount getPaidTaxes() {
        return this.taxManager.getPaidTaxes();
    }
}
