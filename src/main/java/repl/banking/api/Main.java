package repl.banking.api;

import repl.banking.persistence.*;
import repl.banking.service.*;

public class Main {
    public static void main(String[] args) {
        AccountDAO accountDAO = new AccountDAOImpl();
        TransactionDAO transactionDAO = new TransactionDAOImpl();
        BankingService bankingService = new BankingServiceImpl(accountDAO, transactionDAO);
        new BankRepl(bankingService).run();
    }
}