package repl.banking.domain;

public class Account {
    private int accountId;
    private String pin;
    private double balance;

    public Account(String pin, double balance) {
        this.pin = pin;
        this.balance = balance;
    }

    public Account(int accountId, String pin, double balance) {
        this.accountId = accountId;
        this.pin = pin;
        this.balance = balance;
    }

    // getters
    public int getAccountId() { return accountId; }
    public String getPin() { return pin; }
    public double getBalance() { return balance; }

    // setters
    public void setAccountId(int accountId) { this.accountId = accountId; }
    public void setPin(String pin) { this.pin = pin; }
    public void setBalance(double balance) { this.balance = balance; }

}
