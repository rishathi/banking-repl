package repl.banking.persistence;

import java.sql.Connection;
import java.util.List;

import repl.banking.domain.Transaction;

public interface TransactionDAO {

    int addTransaction(Connection conn, Transaction transaction);
    List<Transaction> findLastFive(int accountId);
}
