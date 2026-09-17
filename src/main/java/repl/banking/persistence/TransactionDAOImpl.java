package repl.banking.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import repl.banking.domain.Transaction;

public class TransactionDAOImpl implements TransactionDAO {
    private static final String INSERT_SQL_NORMAL = "INSERT INTO transaction (account_id, amount, type) VALUES (?, ?, ?);";
    private static final String INSERT_SQL_TRANSFER = "INSERT INTO transaction (account_id, amount, type, destination_account_id) VALUES (?, ?, ?, ?);";
    private static final String FIND_LAST_FIVE =
    "SELECT transaction_id, account_id, amount, type, created_at, destination_account_id " +
    "FROM transaction " +
    "WHERE account_id = ? OR destination_account_id = ? " +
    "ORDER BY created_at DESC " +
    "LIMIT 5";


    // ADD TRANSACTION
    @Override
    public int addTransaction(Connection conn, Transaction transaction) {
        String sql = transaction.getType().equals("TRANSFER")
            ? INSERT_SQL_TRANSFER
            : INSERT_SQL_NORMAL;
        try (PreparedStatement statement = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )) {
            statement.setInt(1, transaction.getAccountId());
            statement.setDouble(2, transaction.getAmount());
            statement.setString(3, transaction.getType());

            if (transaction.getType().equals("TRANSFER")) {
                statement.setInt(4, transaction.getDestinationAccountId());
            }
            statement.executeUpdate();

            // Get generated key
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                int accountId = generatedKeys.getInt(1);
                return accountId;
            }
        }

        } catch (SQLException e) {
            throw databaseError("Could not add account", e);
        }

        return -1;
    }

    // FIND FIVE MOST RECENT TRANSACTIONS
    @Override
    public List<Transaction> findLastFive(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_LAST_FIVE)) {
            statement.setInt(1, accountId);
            statement.setInt(2, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(mapTransactions(resultSet));
                }
            }
            return transactions;
        } catch (SQLException e) {
            throw databaseError("Could not list transactions", e);
        }
    }
    private Transaction mapTransactions(ResultSet resultSet) throws SQLException {
        if(resultSet.getString("type").equals("WITHDRAW") || resultSet.getString("type").equals("DEPOSIT")) {
            return new Transaction(
                    resultSet.getInt("transaction_id"),
                    resultSet.getInt("account_id"),
                    resultSet.getDouble("amount"),
                    resultSet.getString("type"),
                    resultSet.getString("created_at")
            );
        }
        return new Transaction(
                resultSet.getInt("transaction_id"),
                resultSet.getInt("account_id"),
                resultSet.getDouble("amount"),
                resultSet.getString("type"),
                resultSet.getInt("destination_account_id"),
                resultSet.getString("created_at")
        );
    }

    private IllegalStateException databaseError(String message, SQLException cause) {
        return new IllegalStateException(message, cause);
    }
}
