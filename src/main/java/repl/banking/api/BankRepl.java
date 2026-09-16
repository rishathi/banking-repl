package repl.banking.api;

import java.util.*;

import repl.banking.domain.Account;
import repl.banking.service.BankingService;

public class BankRepl {
    private final Scanner sc = new Scanner(System.in);
    private final BankingService bankingService;
    private Account loggedIn = null;

    public BankRepl(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    private void login() {
        System.out.println("Welcome to the Bank!\n");
        while(loggedIn == null) {
            System.out.println("Enter 'login' to login to an existing acccount.");
            System.out.println("Enter 'register' to register a new acccount.");
            System.out.println("Enter 'help' to see available commands.");
            System.out.print("\n> ");

            String command = sc.nextLine();
            command.trim();

            if(command.equals("exit")) {
                return;
            }

            try {
                switch(command) {
                    case "login" -> handleLogin();
                    case "register" -> handleRegister();
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handleLogin() {
        System.out.println("Enter account id: ");
        int accountId = Integer.parseInt(sc.nextLine().trim());


        System.out.println("Enter account pin: ");
        String pin = sc.nextLine().trim();

        Account account = bankingService.login(accountId, pin);
        if(account != null) {
            loggedIn = account;
            System.out.println("Logged into account: " + accountId);
        }
        else {
            System.out.println("Unable to log into account: " + accountId);
        }
    }

    private void handleRegister() {
        System.out.println("Set account pin: ");
        String pin = sc.nextLine().trim();

        while (!pin.matches("\\d{4}")) {
            System.out.println("Invalid PIN. Please enter exactly 4 digits: ");
            pin = sc.nextLine().trim();
        }

        Account account = bankingService.register(pin);
        if(account != null) {
            loggedIn = account;
            System.out.println("Created new account. You are logged in.");
            System.out.println("Account Id: " + loggedIn.getAccountId());
        }
        else {
            System.out.println("Unable to log into account.");
        }
    }

    public void run() {
        login();
        while(loggedIn != null) {
            System.out.print("\n> ");
            String command = sc.nextLine();
            command.trim();

            if(command.equals("exit")) {
                return;
            }

            try {
                handle(command);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handle (String command) {
        switch (command){
            case "help" -> printHelp();
            case "logout" -> {
                loggedIn = null;
                System.out.println("Logged out.");
            }
            case "check" -> {
                System.out.println("Balance for Account " + loggedIn.getAccountId() + " = " + loggedIn.getBalance());
            }
            case "withdraw" -> {
                System.out.println("Enter withdraw amount: ");
                double amt = Double.parseDouble(sc.nextLine().trim());

                Account newAccount = bankingService.withdraw(loggedIn, amt);
                if(newAccount != null) {
                    loggedIn = newAccount;
                    System.out.println("Withdraw successful.");
                    System.out.println("Current balance: " + loggedIn.getBalance());
                } else {
                    System.out.println("Withdraw failed. Check account balance.");
                }
            }
            case "deposit" -> {
                System.out.println("Enter deposit amount: ");
                double amt = Double.parseDouble(sc.nextLine().trim());
                
                Account newAccount = bankingService.deposit(loggedIn, amt);
                if(newAccount != null) {
                    loggedIn = newAccount;
                    System.out.println("Deposit successful.");
                    System.out.println("Current balance: " + loggedIn.getBalance());
                } else {
                    System.out.println("Deposit failed.");
                }
            }
            case "transfer" -> {
                System.out.println("Enter the ID of the account to transfer to: ");
                int destAccountId = Integer.parseInt(sc.nextLine().trim());
                System.out.println("Transfer Amount: ");
                double amt = Double.parseDouble(sc.nextLine().trim());
                
                Account newAccount = bankingService.transfer(loggedIn, destAccountId, amt);
                if(newAccount != null) {
                    loggedIn = newAccount;
                    System.out.println("Transfer successful.");
                    System.out.println("Current balance: " + loggedIn.getBalance());
                } else {
                    System.out.println("Deposit failed.");
                }
            }
            case "history" -> {
                bankingService.lastFiveTransactions(loggedIn)
                    .forEach(System.out::println);
            }
        }
    }

    private void printHelp() {
        System.out.println("Available Commands: ");
        System.out.println("exit - Exit the application");

        if(loggedIn != null) {
            System.out.println("logout - Logout of account");
            System.out.println("check - Check account balance");
            System.out.println("withdraw - Withdraw money from your account");
            System.out.println("deposit - Deposit money to your");
            System.out.println("transfer - Transfer money from one account to another");
            System.out.println("history - View last five transactions");
        }
        else {
            System.out.println("login - Login to existing account");
            System.out.println("register - Register a new account");
        }
    }
    
}
