package trading.persistence.account;

import trading.domain.*;
import trading.domain.account.Account;
import trading.domain.account.AccountId;
import trading.domain.account.AccountNotFoundException;
import trading.domain.account.AccountRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// todo transactions?

public class MySqlAccountRepository implements AccountRepository {
    private Connection openNewConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost/trading?user=root&password=testtest&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
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
        // TODO test account id must be set

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
        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into transaction (Id, AccountId, TransactionTypeId, Quantity, TotalPrice, Commission, Isin, Created) " +
                        "values (default, ?, ?, ?, ?, ?, ?, now())", Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setInt(1, accountId.getValue());
        preparedStatement.setInt(2, transaction.getTransactionType().ordinal());
        preparedStatement.setInt(3, transaction.getQuantity().getValue());
        preparedStatement.setDouble(4, transaction.getTotalPrice().getValue());
        preparedStatement.setDouble(5, transaction.getCommission().getValue());
        preparedStatement.setString(6, transaction.getIsin().getText());

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
        PreparedStatement preparedStatement = connection.prepareStatement("select Id, AccountId, TransactionTypeId, Quantity, TotalPrice, Commission, Isin, Created from transaction where AccountId = ?");
        preparedStatement.setInt(1, accountId.getValue());

        ResultSet resultSet = preparedStatement.executeQuery();
        List<Transaction> transactions = new ArrayList<>();

        while(resultSet.next()) {
            int transactionType = resultSet.getInt(3);
            int quantity = resultSet.getInt(4);
            double totalPrice = resultSet.getDouble(5);
            double commission = resultSet.getDouble(6);
            String isin = resultSet.getString(7);

            Transaction transaction = new Transaction(TransactionType.values()[transactionType], new ISIN(isin), new Quantity(quantity), new Amount(totalPrice), new Amount(commission));
            transactions.add(transaction);
        }

        return transactions;
    }
}
