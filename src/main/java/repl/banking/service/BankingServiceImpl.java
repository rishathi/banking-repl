package repl.banking.service;

import repl.banking.persistence.*;

import java.util.List;

import repl.banking.domain.*;

public class BankingServiceImpl implements BankingService {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public BankingServiceImpl(AccountDAO accountDAO, TransactionDAO transactionDAO) {
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
    }

    @Override
    public Account login(int accountId, String pin) {
        Account account = accountDAO.findById(accountId);

        if(account != null && 
            account.getPin().equals(pin) && 
            account.getAccountId() == accountId
        ) {
            return account;
        }
        return null;
    }

    @Override
    public Account register(String pin) {
        Account account = new Account(pin, 0.0);
        int accountId = accountDAO.addAccount(account);

        if(accountId == -1) return null;
        
        account.setAccountId(accountId);
        return account;
    }

    @Override
    public Account deposit(Account account, Double amt) {
        if(accountDAO.findById(account.getAccountId()) != null) {
            Account newAccount = accountDAO.updateBalance(account, account.getBalance() + amt);
            // FIXME: or maybe here addTransfer(all the info, including that its a deposit)
            if(newAccount != null) {
                return newAccount;
            }
        }
        return null;
    }

    @Override
    public Account withdraw(Account account, Double amt) {
        if(accountDAO.findById(account.getAccountId()) != null && account.getBalance() >= amt) {
            Account newAccount = accountDAO.updateBalance(account, account.getBalance() - amt);
            // FIXME: or maybe here addTransfer(all the info, including that its a withdraw)
            if(newAccount != null) {
                return newAccount;
            }
        }
        return null;
    }

    // FIXME
    @Override
    public Account transfer(Account sourceAccount, int destAccountId, Double amt) {
        Account destinationAcccount = accountDAO.findById(destAccountId);
        if(destinationAcccount != null && sourceAccount.getBalance() >= amt) {
            Account newSourceAccount = accountDAO.updateBalance(sourceAccount, sourceAccount.getBalance() - amt);
            Account newDestinationAccount = accountDAO.updateBalance(destinationAcccount, destinationAcccount.getBalance() + amt);

            if(newSourceAccount != null && newDestinationAccount != null) {
                return newSourceAccount;
            }
        }
        return null;
    }

    @Override
    public List<Transaction> lastFiveTransactions(Account account) {
        return transactionDAO.findLastFive(account.getAccountId());
    }
}
