package repl.banking.persistence;

import repl.banking.domain.Account;

public interface AccountDAO {
    int addAccount(Account account);
    Account findById(int accountId);
    Account updateBalance(Account account, Double newBalance);
}
