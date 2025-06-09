package com.example.emanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Centralized data manager for the eManager app
 * Handles all data operations including reset functionality
 */
public class DataManager {
    
    private static final String PREFS_NAME = "eManagerPrefs";
    private static final String PREFS_TRANSACTIONS = "eManagerTransactions";
    private static final String PREFS_NOTES = "eManagerNotes";
    private static final String PREFS_TODOS = "eManagerTodos";
    
    private static DataManager instance;
    private Context context;
    private Map<String, Account> accounts;
    private List<Transaction> transactions;
    
    private DataManager(Context context) {
        this.context = context.getApplicationContext();
        this.accounts = new HashMap<>();
        this.transactions = new ArrayList<>();
        loadAccounts();
    }
    
    private void loadAccounts() {
        SharedPreferences prefs = context.getSharedPreferences("eManagerAccounts", Context.MODE_PRIVATE);
        
        // Check if accounts have been initialized before
        if (prefs.getBoolean("accounts_initialized", false)) {
            // Try to load actual accounts from SharedPreferences
            try {
                // In a real app, you'd deserialize all accounts properly
                // For now, we'll load the accounts individually with their actual balances
                accounts.clear(); // Clear any existing account data
                
                // Get all possible account IDs (in a real app, you would have a better storage system)
                String[] accountIds = {"1", "2", "3"};
                
                boolean accountsLoaded = false;
                
                for (String id : accountIds) {
                    String name = prefs.getString("account_" + id + "_name", null);
                    String type = prefs.getString("account_" + id + "_type", null);
                    float balance = prefs.getFloat("account_" + id + "_balance", 0.0f);
                    String currency = prefs.getString("account_" + id + "_currency", "₹");
                    String description = prefs.getString("account_" + id + "_description", "");
                    
                    if (name != null) {
                        accounts.put(id, new Account(id, name, type, balance, currency, new Date(), description));
                        accountsLoaded = true;
                    }
                }
                
                // If we couldn't load any accounts, initialize defaults
                if (!accountsLoaded) {
                    initializeDefaultAccounts();
                }
            } catch (Exception e) {
                // If anything goes wrong, just create default accounts
                initializeDefaultAccounts();
            }
        } else {
            // First run - initialize default accounts
            initializeDefaultAccounts();
        }
    }
    
    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }
    
    /**
     * Performs a complete app reset - clears all data
     */
    public void performCompleteReset() {
        // First clear in-memory data
        transactions.clear();
        accounts.clear();
        
        // Then clear all persisted data
        clearAllSharedPreferences();
        clearTransactionData();
        clearNotesData();
        clearTodoData();
        clearStatsData();
        clearAccountsData();
        clearSalaryData();
        clearGoalsData();
        clearSettingsData();
        clearCalendarData();
        
        // Reinitialize with default accounts (with zero balances)
        initializeDefaultAccounts();
        
        // Force save account data with zero balances
        for (Account account : accounts.values()) {
            saveAccount(account);
        }
        
        // Mark as first run after reset
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean("first_run_after_reset", true).apply();
        
        // Force balance recalculation from transactions (should be zero since transactions are cleared)
        synchronizeAccounts();
    }
    
    /**
     * Initialize default accounts after reset
     */
    private void initializeDefaultAccounts() {
        // Create default accounts with zero balances
        accounts.put("1", new Account("1", "Main Bank Account", "bank", 0.0, "₹", new Date(), "Primary savings account"));
        accounts.put("2", new Account("2", "Cash Wallet", "cash", 0.0, "₹", new Date(), "Daily expenses cash"));
        accounts.put("3", new Account("3", "Credit Card", "card", 0.0, "₹", new Date(), "Credit card balance"));
        
        // Save the accounts
        SharedPreferences prefs = context.getSharedPreferences("eManagerAccounts", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("accounts_initialized", true).apply();
        
        // Save each account
        for (Account account : accounts.values()) {
            saveAccount(account);
        }
    }
    
    /**
     * Clears all SharedPreferences files
     */
    private void clearAllSharedPreferences() {
        // Clear main preferences
        SharedPreferences mainPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mainPrefs.edit().clear().apply();
        
        // Clear transaction preferences
        SharedPreferences transactionPrefs = context.getSharedPreferences(PREFS_TRANSACTIONS, Context.MODE_PRIVATE);
        transactionPrefs.edit().clear().apply();
        
        // Clear notes preferences
        SharedPreferences notesPrefs = context.getSharedPreferences(PREFS_NOTES, Context.MODE_PRIVATE);
        notesPrefs.edit().clear().apply();
        
        // Clear todo preferences
        SharedPreferences todoPrefs = context.getSharedPreferences(PREFS_TODOS, Context.MODE_PRIVATE);
        todoPrefs.edit().clear().apply();
        
        // Clear statistics preferences
        SharedPreferences statsPrefs = context.getSharedPreferences("eManagerStats", Context.MODE_PRIVATE);
        statsPrefs.edit().clear().apply();
        
        // Clear accounts preferences
        SharedPreferences accountsPrefs = context.getSharedPreferences("eManagerAccounts", Context.MODE_PRIVATE);
        accountsPrefs.edit().clear().apply();
        
        // Clear salary/income preferences
        SharedPreferences salaryPrefs = context.getSharedPreferences("eManagerSalary", Context.MODE_PRIVATE);
        salaryPrefs.edit().clear().apply();
        
        // Clear financial goals preferences
        SharedPreferences goalsPrefs = context.getSharedPreferences("eManagerGoals", Context.MODE_PRIVATE);
        goalsPrefs.edit().clear().apply();
        
        // Clear any other potential preferences files
        SharedPreferences settingsPrefs = context.getSharedPreferences("eManagerSettings", Context.MODE_PRIVATE);
        settingsPrefs.edit().clear().apply();
        
        // Clear calendar preferences
        SharedPreferences calendarPrefs = context.getSharedPreferences("eManagerCalendar", Context.MODE_PRIVATE);
        calendarPrefs.edit().clear().apply();
    }
    
    /**
     * Clears transaction data
     */
    private void clearTransactionData() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_TRANSACTIONS, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
    
    /**
     * Clears notes data
     */
    private void clearNotesData() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NOTES, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
    
    /**
     * Clears todo/wishlist data
     */
    private void clearTodoData() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_TODOS, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
    
    /**
     * Clears stats data (resets to default)
     */
    private void clearStatsData() {
        // Since stats are currently hardcoded, we'll prepare for future implementation
        SharedPreferences prefs = context.getSharedPreferences("eManagerStats", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
    
    /**
     * Checks if this is the first run after reset
     */
    public boolean isFirstRunAfterReset() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean("first_run_after_reset", true);
    }
    
    /**
     * Marks that the app has been initialized after reset
     */
    public void markInitialized() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean("first_run_after_reset", false).apply();
    }
    
    /**
     * Gets the current app version for migration purposes
     */
    public int getAppVersion() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt("app_version", 1);
    }
    
    /**
     * Sets the app version
     */
    public void setAppVersion(int version) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt("app_version", version).apply();
    }
    
    /**
     * Clears accounts data
     */
    private void clearAccountsData() {
        SharedPreferences prefs = context.getSharedPreferences("eManagerAccounts", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
    
    /**
     * Clears salary/income data
     */
    private void clearSalaryData() {
        SharedPreferences prefs = context.getSharedPreferences("eManagerSalary", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
    
    /**
     * Clears financial goals data
     */
    private void clearGoalsData() {
        SharedPreferences prefs = context.getSharedPreferences("eManagerGoals", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
    
    /**
     * Clears settings data
     */
    private void clearSettingsData() {
        SharedPreferences prefs = context.getSharedPreferences("eManagerSettings", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
    
    /**
     * Clears calendar data
     */
    private void clearCalendarData() {
        SharedPreferences prefs = context.getSharedPreferences("eManagerCalendar", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
    
    /**
     * Adds a transaction and updates the relevant account balance
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        
        // Update account balance based on transaction type
        String accountName = transaction.getAccount().toLowerCase();
        String accountId = getAccountIdByName(accountName);
        
        if (accountId != null) {
            Account account = accounts.get(accountId);
            if (account != null) {
                if ("income".equals(transaction.getType())) {
                    account.setBalance(account.getBalance() + transaction.getAmount());
                } else if ("expense".equals(transaction.getType())) {
                    account.setBalance(account.getBalance() - transaction.getAmount());
                }
                saveAccount(account);
            }
        }
        
        // Save transaction to SharedPreferences
        saveTransactions();
        
        // Synchronize all accounts to maintain consistency
        synchronizeAccounts();
    }
    
    /**
     * Save account updates to SharedPreferences
     */
    private void saveAccount(Account account) {
        if (account == null || account.getId() == null) {
            return;
        }
        
        SharedPreferences prefs = context.getSharedPreferences("eManagerAccounts", Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        
        // Save all account data fields
        editor.putString("account_" + account.getId() + "_name", account.getName());
        editor.putString("account_" + account.getId() + "_type", account.getType());
        editor.putFloat("account_" + account.getId() + "_balance", (float) account.getBalance());
        editor.putString("account_" + account.getId() + "_currency", account.getCurrency());
        editor.putString("account_" + account.getId() + "_description", account.getDescription());
        
        // Mark accounts as initialized
        editor.putBoolean("accounts_initialized", true);
        
        // Apply changes
        editor.apply();
    }
    
    /**
     * Save all transactions to SharedPreferences
     */
    private void saveTransactions() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_TRANSACTIONS, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt("transaction_count", transactions.size());
        // In a real app, you'd serialize each transaction
        editor.apply();
    }
    
    /**
     * Get account ID by name (case-insensitive partial match)
     */
    private String getAccountIdByName(String name) {
        // Default mappings for common account types
        if (name.contains("cash")) return "2";
        if (name.contains("card") || name.contains("credit")) return "3";
        return "1"; // Default to main bank account
    }
    
    /**
     * Get all accounts
     */
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }
    
    /**
     * Add a new account
     */
    public void addAccount(Account account) {
        accounts.put(account.getId(), account);
        saveAccount(account);
    }
    
    /**
     * Get all transactions
     */
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }
    
    /**
     * Get total balance across all accounts
     */
    public double getTotalBalance() {
        double total = 0;
        for (Account account : accounts.values()) {
            total += account.getBalance();
        }
        return total;
    }
    
    /**
     * Get financial summary from all transactions
     * @return An array of doubles: [totalIncome, totalExpense, netBalance]
     */
    public double[] getFinancialSummary() {
        double totalIncome = 0.0;
        double totalExpense = 0.0;

        for (Transaction transaction : transactions) {
            if ("income".equals(transaction.getType())) {
                totalIncome += transaction.getAmount();
            } else if ("expense".equals(transaction.getType())) {
                totalExpense += transaction.getAmount();
            }
        }

        double netBalance = totalIncome - totalExpense;
        return new double[] { totalIncome, totalExpense, netBalance };
    }
    
    /**
     * Recalculate account balances based on existing transaction data
     * No transactions are added, removed, or modified by this method
     * This only ensures account balances accurately reflect the transactions
     */
    public void synchronizeAccounts() {
        // Temporarily reset account balances to recalculate them
        // Note: This does NOT delete any transactions, only resets balance numbers
        for (Account account : accounts.values()) {
            account.setBalance(0.0);
        }
        
        // Re-apply all existing transactions to correctly calculate account balances
        // No transactions are modified during this process
        for (Transaction transaction : transactions) {
            String accountName = transaction.getAccount().toLowerCase();
            String accountId = getAccountIdByName(accountName);
            
            if (accountId != null) {
                Account account = accounts.get(accountId);
                if (account != null) {
                    if ("income".equals(transaction.getType())) {
                        account.setBalance(account.getBalance() + transaction.getAmount());
                    } else if ("expense".equals(transaction.getType())) {
                        account.setBalance(account.getBalance() - transaction.getAmount());
                    }
                }
            }
        }
        
        // Save the recalculated balances
        // Again, no transactions are modified, only account balance totals
        for (Account account : accounts.values()) {
            saveAccount(account);
        }
    }
    

}