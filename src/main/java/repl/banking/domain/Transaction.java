package repl.banking.domain;

public class Transaction {
    private int transactionId;
    private int accountId;
    private double amount;
    private String type;
    private String createdAt; // FIXME: does this need to be class datetime?
    private int destinationAccountId;

    // WITHOUT ID & CREATED_AT
    // Constructor for Deposit & Withdraw (without destination account)
    public Transaction(int accountId, double amount, String type) {
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
    }

    // Constructor for Transfer (with destination account)
    public Transaction(int accountId, double amount, String type, int destinationAccountId) {
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.destinationAccountId = destinationAccountId;
    }

    // WITH ID & CREATED_AT
        // Constructor for Deposit & Withdraw (without destination account)
    public Transaction(int transactionId, int accountId, double amount, String type, String createdAt) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    // Constructor for Transfer (with destination account)
    public Transaction(int transactionId, int accountId, double amount, String type, int destinationAccountId, String createdAt) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.destinationAccountId = destinationAccountId;
        this.createdAt = createdAt;
    }

    // getters
    public int getTransactionId() { return transactionId; }
    public int getAccountId() { return accountId; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String geCreatedAt() { return createdAt; }
    public int getDestinationAccountId() { return destinationAccountId; }

    @Override
    public String toString() {
        if(type.equals("TRANSFER")) {
            return String.format("Transaction ID: %d | Source Account: %d | Destination Account: %d | Amount: %.2f | Type: %s | Created At: %s", transactionId, accountId, destinationAccountId, amount, type, createdAt);
        }
        return String.format("Transaction ID: %d | Source Account: %d | Destination Account: - | Amount: %.2f | Type: %s | Created At: %s", transactionId, accountId, amount, type, createdAt);
    }

}