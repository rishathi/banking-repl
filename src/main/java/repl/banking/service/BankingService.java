package repl.banking.service;

import repl.banking.domain.*;
import java.util.*;

public interface BankingService {
    Account login(int accountId, String pin);
    Account register(String pin);
    Account withdraw(Account account, Double amt);
    Account deposit(Account account, Double amt);
    Account transfer(Account sourceAccount, int destAccountId, Double amt);
    List<Transaction> lastFiveTransactions(Account account);
}
