package repl.banking.service;

import repl.banking.persistence.*;
import repl.banking.util.BankLogger;

import java.sql.Connection;
import java.sql.SQLException;
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
        try {
            Account account = accountDAO.findById(accountId);
            if(account != null && 
                account.getAccountId() == accountId
            ) {
                if(account.getPin().equals(pin)) {
                    // LOG SUCCEEDED
                    BankLogger.info(
                        "Login Succeeded for " + account.getAccountId()
                    );
                    return account;
                }
                // LOG FAILED
                BankLogger.error(
                    "Login Failed for " + accountId
                );

                throw new IllegalArgumentException("Incorrect Pin.");
            }
            // LOG FAILED
            BankLogger.error(
                "Login Failed for " + accountId
            );
            throw new IllegalArgumentException("Incorrect ID.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
        
    }

    @Override
    public Account register(String pin) {
        try {
            Account account = new Account(pin, 0.0);
            int accountId = accountDAO.addAccount(account);

            if(accountId == -1) {
                // LOG FAILED
                BankLogger.error(
                    "Account registration failed."
                );
                throw new SQLException("Account registration failed.");
            }

            // LOG SUCCEEDED
            BankLogger.info(
                "Account registration Succeeded for " + accountId
            );
            account.setAccountId(accountId);
            return account;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Account deposit(Account account, Double amt) {
        if (amt <= 0) {
            // LOG FAILED
            BankLogger.error(
                "Deposit failed: Amount less than zero."
            );
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        // set up one connection to use in accountDAO.updateBalance() & 
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnectionFactory().getConnection();
            conn.setAutoCommit(false);

            // UPDATE ACCOUNT
            Account accountFromDB =
            accountDAO.findById(account.getAccountId());

            if (accountFromDB == null) {
                // LOG FAILED
                BankLogger.error(
                    "Deposit failed: Account does not exist."
                );
                throw new IllegalArgumentException("Account does not exist.");
            }

            Account updatedAccount = accountDAO.updateBalance(conn, accountFromDB, accountFromDB.getBalance() + amt);
            if(updatedAccount == null) {
                // LOG FAILED
                BankLogger.error(
                    "Deposit failed: Balance update failed."
                );
                throw new SQLException("Balance update failed.");
            }

            // INSERT TRANSACTION
            Transaction transaction = new Transaction(accountFromDB.getAccountId(), amt, "DEPOSIT");
            int rowsAffected = transactionDAO.addTransaction(conn, transaction);

            if (rowsAffected == 0) {
                // LOG FAILED
                BankLogger.error(
                    "Deposit failed: Transaction insert failed."
                );
                throw new SQLException("Transaction insert failed.");
            }

            // BOTH SUCCEEDED
            conn.commit();

            // LOG SUCCEEDED
            BankLogger.info(
                "Deposit successful. Account ID: " + account.getAccountId() + " | Amount: " + amt
            );
            return updatedAccount;
        } catch (Exception e) {
            if(conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException sqlE) {
                    sqlE.printStackTrace();
                }
            }
            System.out.println("Error: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Account withdraw(Account account, Double amt) {
        if (amt <= 0) {
            // LOG FAILED
            BankLogger.error(
                "Deposit failed: Amount less than zero."
            );
            throw new IllegalArgumentException("Withdraw amount must be greater than zero.");
        }

        // set up one connection to use in accountDAO.updateBalance() & 
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnectionFactory().getConnection();
            conn.setAutoCommit(false);

            // UPDATE ACCOUNT
            Account accountFromDB =
            accountDAO.findById(account.getAccountId());

            if (accountFromDB == null) {
                // LOG FAILED
                BankLogger.error(
                    "Deposit failed: Account does not exist."
                );
                throw new IllegalArgumentException("Account does not exist.");
            }

            if(accountFromDB.getBalance() < amt) {
                // LOG FAILED
                BankLogger.error(
                    "Deposit failed: Withdraw amount exceeds account balance."
                );
                throw new IllegalArgumentException("Withdraw amount exceeds account balance.");
            }

            Account updatedAccount = accountDAO.updateBalance(conn, accountFromDB, accountFromDB.getBalance() - amt);
            if(updatedAccount == null) {
                // LOG FAILED
                BankLogger.error(
                    "Deposit failed: Balance update failed."
                );
                throw new SQLException("Balance update failed.");
            }

            // INSERT TRANSACTION
            Transaction transaction = new Transaction(accountFromDB.getAccountId(), amt, "WITHDRAW");
            int rowsAffected = transactionDAO.addTransaction(conn, transaction);

            if (rowsAffected == 0) {
                // LOG FAILED
                BankLogger.error(
                    "Deposit failed: Transaction insert failed."
                );
                throw new SQLException("Transaction insert failed.");
            }

            // BOTH SUCCEEDED
            conn.commit();

            // LOG SUCCEEDED
            BankLogger.info(
                "Withdraw successful. Account ID: " + account.getAccountId() + " | Amount: " + amt
            );

            return updatedAccount;
        } catch (Exception e) {
            if(conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException sqlE) {
                    sqlE.printStackTrace();
                }
            }
            System.out.println("Error: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Account transfer(Account sourceAccount, int destAccountId, Double amt) {
        if (amt <= 0) {
            // LOG FAILED
            BankLogger.error(
                "Deposit failed: Amount less than zero."
            );
            throw new IllegalArgumentException("Withdraw amount must be greater than zero.");
        }

        // set up one connection to use in accountDAO.updateBalance() & 
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnectionFactory().getConnection();
            conn.setAutoCommit(false);

            // UPDATE SOURCE ACCOUNT
            Account sourceAccountFromDB =
            accountDAO.findById(sourceAccount.getAccountId());

            if (sourceAccountFromDB == null) {
                // LOG FAILED
                BankLogger.error(
                    "Deposit failed: Source account does not exist."
                );
                throw new IllegalArgumentException("Source account does not exist.");
            }

            if(sourceAccountFromDB.getBalance() < amt) {
                // LOG FAILED
                BankLogger.error(
                    "Deposit failed: Transfer amount exceeds source account balance."
                );
                throw new IllegalArgumentException("Transfer amount exceeds source account balance.");
            }

            Account updatedSourceAccount = accountDAO.updateBalance(conn, sourceAccountFromDB, sourceAccountFromDB.getBalance() - amt);
            if(updatedSourceAccount == null) {
                // LOG FAILED
                BankLogger.error(
                    "Deposit failed: Balance update failed."
                );
                throw new SQLException("Balance update failed.");
            }

            // UPDATE DESTINATION ACCOUNT
            Account destAccountFromDB =
            accountDAO.findById(destAccountId);

            if (destAccountFromDB == null) {
                // LOG FAILED
                BankLogger.error(
                    "Deposit failed: Destination account does not exist."
                );
                throw new IllegalArgumentException("Destination account does not exist.");
            }

            Account updatedDestAccount = accountDAO.updateBalance(conn, destAccountFromDB, destAccountFromDB.getBalance() + amt);
            if(updatedDestAccount == null) {
                // LOG FAILED
                BankLogger.error(
                    "Deposit failed: Balance update failed."
                );
                throw new SQLException("Balance update failed.");
            }

            // INSERT TRANSACTION
            Transaction transaction = new Transaction(sourceAccountFromDB.getAccountId(), amt, "TRANSFER", destAccountFromDB.getAccountId());
            int rowsAffected = transactionDAO.addTransaction(conn, transaction);

            if (rowsAffected == 0) {
                // LOG FAILED
                BankLogger.error(
                    "Deposit failed: Transaction insert failed."
                );
                throw new SQLException("Transaction insert failed.");
            }

            // BOTH SUCCEEDED
            conn.commit();

            // LOG SUCCEEDED
            BankLogger.info(
                "Transfer successful. Source Account ID: " + updatedSourceAccount.getAccountId() + " | Destination Account ID: " + updatedDestAccount.getAccountId() + " | Amount: " + amt
            );

            return updatedSourceAccount;
        } catch (Exception e) {
            if(conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException sqlE) {
                    sqlE.printStackTrace();
                }
            }
            System.out.println("Error: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<Transaction> lastFiveTransactions(Account account) {
        return transactionDAO.findLastFive(account.getAccountId());
    }
}
