package trading.persistence.account;

import trading.domain.*;
import trading.domain.account.*;
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
    public Account createAccount(ClientId clientId, Amount seedCapital) {
        Connection connection = this.openNewConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into account (Id, ClientId, SeedCapital) values (default, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, clientId.getValue());
            preparedStatement.setDouble(2, seedCapital.getValue());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int accountId = resultSet.getInt(1);

            Account account = new Account(seedCapital);
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
            for(MarketTransaction transaction: account.getProcessedTransactions()) {
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

    private void saveTransaction(Connection connection, AccountId accountId, MarketTransaction transaction) throws SQLException {
        if(transaction.getDate() == null) {
            throw new RuntimeException("The transaction date has to be set so that it can be persisted.");
        }

        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into transaction (Id, AccountId, TransactionTypeId, Quantity, TotalPrice, Commission, Isin, `Date`, Created) " +
                        "values (default, ?, ?, ?, ?, ?, ?, ?, now())", Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setInt(1, accountId.getValue());
        preparedStatement.setInt(2, transaction.getTransactionType().ordinal());
        preparedStatement.setInt(3, transaction.getQuantity().getValue());
        preparedStatement.setDouble(4, transaction.getTotalPrice().getValue());
        preparedStatement.setDouble(5, transaction.getCommission().getValue());
        preparedStatement.setString(6, transaction.getIsin().getText());
        preparedStatement.setString(7, transaction.getDate().toString());

        preparedStatement.executeUpdate();

        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        resultSet.next();
        int transactionId = resultSet.getInt(1);
        transaction.setId(new TransactionId(transactionId));
    }

    @Override
    public Account getAccount(AccountId accountId) {
        Connection connection = this.openNewConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select Id, ClientId, SeedCapital from account where Id = ?");
            preparedStatement.setInt(1, accountId.getValue());

            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()) {
                throw new AccountNotFoundException();
            }

            double seedCapital = resultSet.getDouble(3);

            Account account = new Account(new Amount(seedCapital));
            account.setId(accountId);

            List<MarketTransaction> transactions = this.getAccountTransactions(connection, accountId);

            for(MarketTransaction transaction: transactions) {
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

    private List<MarketTransaction> getAccountTransactions(Connection connection, AccountId accountId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select Id, AccountId, TransactionTypeId, Quantity, TotalPrice, Commission, Isin, Date, Created from transaction where AccountId = ?");
        preparedStatement.setInt(1, accountId.getValue());

        ResultSet resultSet = preparedStatement.executeQuery();
        List<MarketTransaction> transactions = new ArrayList<>();

        while(resultSet.next()) {
            int transactionId = resultSet.getInt(1);
            int transactionType = resultSet.getInt(3);
            int quantity = resultSet.getInt(4);
            double totalPrice = resultSet.getDouble(5);
            double commission = resultSet.getDouble(6);
            String isin = resultSet.getString(7);
            LocalDate date = LocalDate.parse(resultSet.getString(8));

            MarketTransaction transaction = new MarketTransactionBuilder()
                    .setTransactionType(TransactionType.values()[transactionType])
                    .setIsin(new ISIN(isin))
                    .setQuantity(new Quantity(quantity))
                    .setTotalPrice(new Amount(totalPrice))
                    .setCommission(new Amount(commission))
                    .setDate(date)
                    .build();

            transaction.setId(new TransactionId(transactionId));

            transactions.add(transaction);
        }

        return transactions;
    }
}
