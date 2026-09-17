# Project: Bank Application (REPL)

## 1. Overview
A functional banking application that runs entirely within the terminal. 

## 2. Functionality
*   **Secure Access:** Users must can register and log in using a unique `Account ID` and `PIN`.
*   **Balance Management:** Users can check their current account balance at any time.
*   **The Transaction Engine:**
    *   `Deposit`: Add funds to an account.
    *   `Withdraw`: Remove funds (the system must prevent overdrawing!).
    *   `Transfer`: Move money securely between two different accounts.
*   **Audit Trail:** Users can view their recent transaction history.
*   **System Logging:** The application maintains a log file to track activity. 
    *   `INFO`: To record successful actions (e.g., "User successfully logged in").
    *   `ERROR`: To record failures or security risks (e.g., "Incorrect PIN entered" or "Database connection lost").

## 3. Architecture & Tech Stack
This application follows **Layered Architecture**. 

1.  **API Layer (The Interface):** This is what the user sees. It handles all terminal inputs, menu navigation, and printing messages. This layer *only* talks to the Service Layer.
2.  **Business Layer (The Brains):** This is where the banking rules live (e.g., "Can this user afford this withdrawal?"). It receives calls from the API and calls the Repository Layer
3.  **Repository Layer (The Vault):** This layer handles all communication with the SQL database. It converts SQL rows into Java objects and vice versa. It *only* receives calls from the Business Layer.

### The Tech Stack
*   **Language:** Java
*   **Build Tool:** Maven
*   **Database:** Postgres
*   **Testing:** JUnit 5
*   **Version Control:** Git & GitHub

## 4. Testing Strategies
Currently contains two JUnit 5 tests: Sunny and Rainy day tests for the Login functionality.