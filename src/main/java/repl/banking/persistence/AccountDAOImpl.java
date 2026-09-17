package repl.banking.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import repl.banking.domain.Account;

public class AccountDAOImpl implements AccountDAO {
    private static final String INSERT_SQL = "INSERT INTO account (pin, balance) VALUES (?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT account_id, pin, balance FROM account WHERE account_id = ?";
    private static final String UPDATE_SQL = "UPDATE account SET balance = ? WHERE account_id = ?";

    // ADD ACCOUNT
    @Override
    public int addAccount(Account account) {
        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                    INSERT_SQL,
                    Statement.RETURN_GENERATED_KEYS
                )) {
            statement.setString(1, account.getPin());
            statement.setDouble(2, 0.0);
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

    // FIND ACCOUNT BY ID
    @Override
    public Account findById(int accountId) {
        // try establishing a connection with the db
        try(Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
            PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
                statement.setInt(1, accountId); // replaces the first '?' with the value of accountId

                // try excecuting the query, which returns a resultSet
                try (ResultSet resultSet = statement.executeQuery()) {
                    // if the database found an account
                    if (resultSet.next()) { // Initially, the ResultSet cursor is positioned before the first row. Moves cursor to the next row
                        return mapAccount(resultSet);
                    }
                }
                return null;
        } catch (SQLException e) {
            throw databaseError("Could not find account", e);
        }
    }
    private Account mapAccount(ResultSet resultSet) throws SQLException {
        return new Account(
                resultSet.getInt("account_id"),
                resultSet.getString("pin"),
                resultSet.getDouble("balance"));
    }

    // UPDATE ACCOUNT BALANCE
    @Override 
    public Account updateBalance(Connection conn, Account account, Double newBalance) {
        try (PreparedStatement statement = conn.prepareStatement(UPDATE_SQL)) {
            statement.setDouble(1, newBalance); // balance
            statement.setInt(2, account.getAccountId()); // account id
            if(statement.executeUpdate() == 1) {
                account.setBalance(newBalance);
                return account;
            }
            return null;
        } catch (SQLException e) {
            throw databaseError("Could not update Account balance.", e);
        }
    }


    private IllegalStateException databaseError(String message, SQLException cause) {
        return new IllegalStateException(message, cause);
    }
}
