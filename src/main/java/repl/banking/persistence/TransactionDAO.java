package repl.banking.persistence;

import java.util.List;

import repl.banking.domain.Transaction;

public interface TransactionDAO {

    int addTransaction(Transaction transaction);
    List<Transaction> findLastFive(int accountId);
}
