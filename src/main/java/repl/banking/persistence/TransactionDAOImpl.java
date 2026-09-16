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
    // FIXME: for FIND_LAST_FIVE, include destination_account_id = input_id also
    private static final String FIND_LAST_FIVE = "SELECT transaction_id, account_id, amount, type, created_at, destination_account_id FROM transaction ORDER BY created_at LIMIT 5";

    // ADD TRANSACTION
    @Override
    public int addTransaction(Transaction transaction) {
        String sql = transaction.getType().equals("TRANSFER")
            ? INSERT_SQL_TRANSFER
            : INSERT_SQL_NORMAL;
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
                PreparedStatement statement = connection.prepareStatement(
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
        List<Transaction> students = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_LAST_FIVE);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                students.add(mapTransactions(resultSet));
            }
            return students;
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
                resultSet.getInt("destinationAccountId"),
                resultSet.getString("created_at")
        );
    }

    private IllegalStateException databaseError(String message, SQLException cause) {
        return new IllegalStateException(message, cause);
    }
}
