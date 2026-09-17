package repl.banking.persistence;

import java.sql.Connection;

import repl.banking.domain.Account;

public interface AccountDAO {
    int addAccount(Account account);
    Account findById(int accountId);
    Account updateBalance(Connection conn, Account account, Double newBalance);
}
