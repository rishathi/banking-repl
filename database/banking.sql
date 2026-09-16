-- Create Database for Banking REPL
CREATE DATABASE banking;

-- Create Account
CREATE TABLE account (
    account_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pin VARCHAR(255) NOT NULL,
    balance NUMERIC(12, 2) NOT NULL DEFAULT 0.00
);

-- Create Transaction
CREATE TABLE transaction (
    transaction_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_id INTEGER NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    destination_account_id INTEGER,

    CONSTRAINT fk_transaction_account 
        FOREIGN KEY (account_id) 
        REFERENCES account(account_id),

    CONSTRAINT fk_transaction_destination
        FOREIGN KEY (destination_account_id)
        REFERENCES account(account_id)
);

-- Test account
INSERT INTO account (pin, balance)
VALUES
    ('1234', 500.00),
    ('5678', 250.00);

SELECT * FROM account;

-- Test transactions
INSERT INTO transaction
    (account_id, amount, type)
VALUES
    (1, 100.00, 'DEPOSIT');

SELECT * FROM transaction;
