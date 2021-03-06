package trading.persistence.account;

import trading.domain.*;
import trading.domain.account.*;
import trading.domain.taxes.ProfitCategories;
import trading.persistence.MySqlRepository;
import trading.persistence.MySqlRepositoryParameters;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MySqlAccountRepository extends MySqlRepository implements AccountRepository {
    public MySqlAccountRepository(MySqlRepositoryParameters parameters) {
        super(parameters);
    }

    @Override
    public Account createAccount(ClientId clientId, Amount seedCapital, TaxStrategy taxStrategy) {
        Connection connection = this.openNewConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into account (Id, ClientId, SeedCapital) values (default, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, clientId.getValue());
            preparedStatement.setDouble(2, seedCapital.getValue());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int accountId = resultSet.getInt(1);

            Account account = new AccountBuilder()
                    .setAvailableMoney(seedCapital)
                    .setTaxStrategy(taxStrategy)
                    .build();

            account.setId(new AccountId(accountId));

            return account;
        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            this.closeConnection(connection);
        }
    }

    @Override
    public void saveAccount(Account account) {
        if(account.getId() == null) {
            throw new RuntimeException("The account id must be set so that the account can be updated.");
        }

        Connection connection = this.openNewConnection();

        try {
            for(Transaction transaction: account.getProcessedTransactions()) {
                if(transaction.getId() == null) {
                    this.saveTransaction(connection, account.getId(), transaction);
                }
            }
        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            closeConnection(connection);
        }
    }

    private void saveTransaction(Connection connection, AccountId accountId, Transaction transaction) throws SQLException {
        if(transaction instanceof MarketTransaction) {
            this.saveMarketTransaction(connection, accountId, (MarketTransaction) transaction);
        }
        else if(transaction instanceof DividendTransaction) {
            this.saveDividendTransaction(connection, accountId, (DividendTransaction) transaction);
        }
        else if(transaction instanceof TaxPaymentTransaction) {
            this.saveTaxPaymentTransaction(connection, accountId, (TaxPaymentTransaction) transaction);
        }
        else {
            throw new RuntimeException("Transaction type not supported.");
        }
    }

    private void saveMarketTransaction(Connection connection, AccountId accountId, MarketTransaction transaction) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into transaction (Id, AccountId, TransactionTypeId, Quantity, TotalPrice, Commission, Isin, `Date`, Created) " +
                    "values (default, ?, ?, ?, ?, ?, ?, ?, now())", Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setInt(1, accountId.getValue());
        preparedStatement.setInt(2, transaction.getTransactionType().getIndex());
        preparedStatement.setInt(3, transaction.getQuantity().getValue());
        preparedStatement.setDouble(4, transaction.getTotalPrice().getValue());
        preparedStatement.setDouble(5, transaction.getCommission().getValue());
        preparedStatement.setString(6, transaction.getIsin().getText());
        preparedStatement.setString(7, transaction.getDate().toString());

        preparedStatement.executeUpdate();

        this.applyGeneratedIdToTransaction(transaction, preparedStatement);
    }

    private void saveDividendTransaction(Connection connection, AccountId accountId, DividendTransaction transaction) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into transaction (Id, AccountId, TransactionTypeId, Amount, Isin, `Date`, Created) " +
                        "values (default, ?, ?, ?, ?, ?, now())", Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setInt(1, accountId.getValue());
        preparedStatement.setInt(2, 2);
        preparedStatement.setDouble(3, transaction.getAmount().getValue());
        preparedStatement.setString(4, transaction.getIsin().getText());
        preparedStatement.setString(5, transaction.getDate().toString());

        preparedStatement.executeUpdate();

        this.applyGeneratedIdToTransaction(transaction, preparedStatement);
    }

    private void saveTaxPaymentTransaction(Connection connection, AccountId accountId, TaxPaymentTransaction transaction) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into transaction (Id, AccountId, TransactionTypeId, ProfitCategoryId, TaxPeriodYear, TaxedProfit, PaidTaxes, `Date`, Created) " +
                        "values (default, ?, ?, ?, ?, ?, ?, ?, now())", Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setInt(1, accountId.getValue());
        preparedStatement.setInt(2, 3);
        preparedStatement.setInt(3, transaction.getProfitCategory().getId());
        preparedStatement.setInt(4, transaction.getTaxPeriodYear());
        preparedStatement.setDouble(5, transaction.getTaxedProfit().getValue());
        preparedStatement.setDouble(6, transaction.getPaidTaxes().getValue());
        preparedStatement.setString(7, transaction.getDate().toString());

        preparedStatement.executeUpdate();

        this.applyGeneratedIdToTransaction(transaction, preparedStatement);
    }

    private void applyGeneratedIdToTransaction(Transaction transaction, PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        resultSet.next();
        int transactionId = resultSet.getInt(1);
        transaction.setId(new TransactionId(transactionId));
    }

    @Override
    public Account getAccount(AccountId accountId, TaxStrategy taxStrategy) {
        Connection connection = this.openNewConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select Id, ClientId, SeedCapital from account where Id = ?");
            preparedStatement.setInt(1, accountId.getValue());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()) {
                throw new AccountNotFoundException();
            }

            double seedCapital = resultSet.getDouble(3);

            Account account = new AccountBuilder()
                    .setAvailableMoney(new Amount(seedCapital))
                    .setTaxStrategy(taxStrategy)
                    .build();

            account.setId(accountId);

            List<Transaction> transactions = this.getAccountTransactions(connection, accountId);

            for(Transaction transaction: transactions) {
                account.registerTransaction(transaction);
            }

            return account;
        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            closeConnection(connection);
        }
    }

    private List<Transaction> getAccountTransactions(Connection connection, AccountId accountId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "select Id, AccountId, TransactionTypeId, Quantity, TotalPrice, Commission, Amount, Isin, Date, ProfitCategoryId, TaxPeriodYear, TaxedProfit, PaidTaxes, Created " +
                        "from transaction where AccountId = ?");
        preparedStatement.setInt(1, accountId.getValue());

        ResultSet resultSet = preparedStatement.executeQuery();
        List<Transaction> transactions = new ArrayList<>();

        while(resultSet.next()) {
            int transactionId = resultSet.getInt(1);
            int transactionTypeIndex = resultSet.getInt(3);
            int quantity = resultSet.getInt(4);
            double totalPrice = resultSet.getDouble(5);
            double commission = resultSet.getDouble(6);
            double amount = resultSet.getDouble(7);
            String isin = resultSet.getString(8);
            LocalDate date = LocalDate.parse(resultSet.getString(9));
            int profitCategoryId = resultSet.getInt(10);
            int taxPeriodYear = resultSet.getInt(11);
            double taxedProfit = resultSet.getInt(12);
            double paidTaxes = resultSet.getDouble(13);

            TransactionType transactionType = TransactionType.ofIndex(transactionTypeIndex);

            Transaction transaction;

            if(transactionType instanceof MarketTransactionType) {
                transaction = new MarketTransactionBuilder()
                        .setTransactionType((MarketTransactionType) transactionType)
                        .setIsin(new ISIN(isin))
                        .setQuantity(new Quantity(quantity))
                        .setTotalPrice(new Amount(totalPrice))
                        .setCommission(new Amount(commission))
                        .setDate(date)
                        .build();
            }
            else if(transactionType == TransactionType.Dividend) {
                transaction = new DividendTransactionBuilder()
                        .setIsin(new ISIN(isin))
                        .setAmount(new Amount(amount))
                        .setDate(date)
                        .build();
            }
            else if(transactionType == TransactionType.TaxPayment) {
                transaction = new TaxPaymentTransactionBuilder()
                        .setDate(date)
                        .setProfitCategory(ProfitCategories.fromId(profitCategoryId))
                        .setTaxPeriodYear(taxPeriodYear)
                        .setTaxedProfit(new Amount(taxedProfit))
                        .setPaidTaxes(new Amount(paidTaxes))
                        .build();
            }
            else {
                throw new RuntimeException("Transaction type not supported.");
            }

            transaction.setId(new TransactionId(transactionId));
            transactions.add(transaction);
        }

        return transactions;
    }
}
