package com.example.emanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.emanager.databinding.ActivityMainBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AddTransactionFragment.OnTransactionAddedListener {

    // SharedPreferences constants
    private static final String PREFS_NAME = "eManagerPrefs";
    private static final String PREF_THEME = "theme_setting";
    private static final String PREF_CURRENCY = "currency_setting";
    private static final String PREF_NOTIFICATIONS = "notifications_enabled";
    private static final String PREF_FIRST_RUN = "first_run";

    private ActivityMainBinding binding;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactions;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault());
    private Date currentDate = new Date();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        setupToolbar();
        setupRecyclerView();
        setupBottomNavigation();
        setupFab();
        setupDateNavigation();
        setupTabLayout();
        loadSampleData();
        updateFinancialSummary();

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("💰 Expense Tracker");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // Handle toolbar navigation click
        binding.toolbar.setNavigationOnClickListener(v -> {
            // You can customize this behavior - for now, let's show a menu or go back
            showNavigationMenu();
        });
    }

    private void setupRecyclerView() {
        transactions = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactions);
        binding.transactionList.setLayoutManager(new LinearLayoutManager(this));
        binding.transactionList.setAdapter(transactionAdapter);
    }

    private void setupBottomNavigation() {
        binding.bottomNavigationView2.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.transactions) {
                // Already on transactions screen
                return true;
            } else if (itemId == R.id.stats) {
                // Navigate to StatsActivity
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.account) {
                // Navigate to Account Management
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.more) {
                // Navigate to WishlistActivity
                Intent intent = new Intent(MainActivity.this, WishlistActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setupFab() {
        binding.floatingActionButton.setOnClickListener(v -> {
            // Check which tab is currently selected
            int selectedTabPosition = binding.tabLayout.getSelectedTabPosition();
            
            // If Notes tab is selected (position 2), show add note dialog
            if (selectedTabPosition == 2) {
                showAddNoteDialog();
            } else {
                // Default behavior - show add transaction dialog
                AddTransactionFragment fragment = new AddTransactionFragment();
                fragment.setOnTransactionAddedListener(this);
                fragment.setTransactionDate(currentDate); // Pass the currently selected date
                fragment.show(getSupportFragmentManager(), "AddTransactionFragment");
            }
        });
    }
    
    /**
     * Show dialog to add a new note
     */
    private void showAddNoteDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
        EditText titleEdit = dialogView.findViewById(R.id.note_title_edit);
        EditText contentEdit = dialogView.findViewById(R.id.note_content_edit);

        new MaterialAlertDialogBuilder(this)
                .setTitle("📝 Add New Note")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = titleEdit.getText().toString().trim();
                    String content = contentEdit.getText().toString().trim();
                    
                    if (title.isEmpty()) {
                        Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (content.isEmpty()) {
                        Toast.makeText(this, "Please enter some content", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Get the current notes fragment
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (currentFragment instanceof NotesFragment) {
                        // Call the fragment's method to add a note
                        ((NotesFragment) currentFragment).addNoteFromMainActivity(title, content);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupDateNavigation() {
        // Set current date with day of week
        updateDateDisplay();
        
        // Previous date button
        binding.previousDateBtn.setOnClickListener(v -> {
            // Move to previous day
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(java.util.Calendar.DAY_OF_MONTH, -1);
            currentDate = calendar.getTime();
            
            // Update the UI
            updateDateDisplay();
            loadTransactionsForDate(currentDate);
        });
        
        // Next date button
        binding.nextDateBtn.setOnClickListener(v -> {
            // Move to next day
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
            currentDate = calendar.getTime();
            
            // Update the UI
            updateDateDisplay();
            loadTransactionsForDate(currentDate);
        });
    }
    
    private void updateDateDisplay() {
        // Update the date display with day of week
        binding.currentDate.setText(dateFormat.format(currentDate));
    }

    private void setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(new com.google.android.material.tabs.TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(com.google.android.material.tabs.TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0: // Daily
                        showDailyView();
                        break;
                    case 1: // Calendar
                        showCalendarView();
                        break;
                    case 2: // Notes
                        showNotesView();
                        break;
                }
            }

            @Override
            public void onTabUnselected(com.google.android.material.tabs.TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(com.google.android.material.tabs.TabLayout.Tab tab) {}
        });
    }

    private void showDailyView() {
        // Show the transaction list and financial summary
        binding.financialSummaryContainer.setVisibility(android.view.View.VISIBLE);
        binding.transactionList.setVisibility(android.view.View.VISIBLE);
        binding.fragmentContainer.setVisibility(android.view.View.GONE);
        
        // Clear any fragments
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new Fragment())
                .commit();
    }



    private void showCalendarView() {
        // Hide daily view components
        binding.financialSummaryContainer.setVisibility(android.view.View.GONE);
        binding.transactionList.setVisibility(android.view.View.GONE);
        binding.fragmentContainer.setVisibility(android.view.View.VISIBLE);
        
        // Show calendar fragment
        CalendarFragment calendarFragment = new CalendarFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, calendarFragment)
                .commit();
    }

    private void showNotesView() {
        // Hide daily view components
        binding.financialSummaryContainer.setVisibility(android.view.View.GONE);
        binding.transactionList.setVisibility(android.view.View.GONE);
        binding.fragmentContainer.setVisibility(android.view.View.VISIBLE);
        
        // Show notes fragment
        NotesFragment notesFragment = new NotesFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, notesFragment)
                .commit();
    }

    private void showSettingsDialog() {
        String[] settingsOptions = {
            "🎨 Theme Settings",
            "💰 Currency Settings", 
            "🔔 Notifications",
            "📊 Data Management",
            "🔒 Privacy & Security",
            "📤 Export Data",
            "📥 Import Data",
            "🔄 Reset App"
        };
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("⚙️ Settings")
                .setItems(settingsOptions, (dialog, which) -> {
                    switch (which) {
                        case 0: // Theme Settings
                            showThemeSettings();
                            break;
                        case 1: // Currency Settings
                            showCurrencySettings();
                            break;
                        case 2: // Notifications
                            showNotificationSettings();
                            break;
                        case 3: // Data Management
                            showDataManagement();
                            break;
                        case 4: // Privacy & Security
                            showPrivacySettings();
                            break;
                        case 5: // Export Data
                            showExportDialog();
                            break;
                        case 6: // Import Data
                            showImportDialog();
                            break;
                        case 7: // Reset App
                            showResetDialog();
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showThemeSettings() {
        String[] themes = {"🌙 Dark Theme", "☀️ Light Theme", "🌈 Auto (System)"};
        String[] themeValues = {"dark", "light", "auto"};
        
        // Get current theme setting
        String currentTheme = getSetting(PREF_THEME, "auto");
        int selectedIndex = 2; // default to auto
        for (int i = 0; i < themeValues.length; i++) {
            if (themeValues[i].equals(currentTheme)) {
                selectedIndex = i;
                break;
            }
        }
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("🎨 Theme Settings")
                .setSingleChoiceItems(themes, selectedIndex, (dialog, which) -> {
                    String selectedTheme = themes[which];
                    String selectedValue = themeValues[which];
                    
                    // Save the setting
                    saveSetting(PREF_THEME, selectedValue);
                    
                    android.widget.Toast.makeText(this, "Theme changed to: " + selectedTheme, android.widget.Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    
                    // Note: Full theme implementation would require app restart or recreation
                    android.widget.Toast.makeText(this, "💡 Restart app to apply theme changes", android.widget.Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCurrencySettings() {
        String[] currencies = {"₹ INR (Indian Rupee)", "$ USD (US Dollar)", "€ EUR (Euro)", "£ GBP (British Pound)", "¥ JPY (Japanese Yen)"};
        String[] currencySymbols = {"₹", "$", "€", "£", "¥"};
        String[] currencyCodes = {"INR", "USD", "EUR", "GBP", "JPY"};
        
        // Get current currency setting
        String currentCurrency = getSetting(PREF_CURRENCY, "INR");
        int selectedIndex = 0; // default to INR
        for (int i = 0; i < currencyCodes.length; i++) {
            if (currencyCodes[i].equals(currentCurrency)) {
                selectedIndex = i;
                break;
            }
        }
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("💰 Currency Settings")
                .setSingleChoiceItems(currencies, selectedIndex, (dialog, which) -> {
                    String selectedCurrency = currencies[which];
                    String selectedCode = currencyCodes[which];
                    String selectedSymbol = currencySymbols[which];
                    
                    // Save the setting
                    saveSetting(PREF_CURRENCY, selectedCode);
                    saveSetting("currency_symbol", selectedSymbol);
                    
                    android.widget.Toast.makeText(this, "Currency changed to: " + selectedCurrency, android.widget.Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    
                    // Update the financial summary with new currency
                    updateFinancialSummary();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showNotificationSettings() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("🔔 Notification Settings")
                .setMessage("📱 Notification preferences:\n\n" +
                           "• Daily expense reminders\n" +
                           "• Budget limit alerts\n" +
                           "• Monthly summary reports\n" +
                           "• Goal achievement notifications\n\n" +
                           "🚧 Full notification settings coming soon!")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showDataManagement() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("📊 Data Management")
                .setMessage("📈 Data management options:\n\n" +
                           "• View storage usage\n" +
                           "• Clear cache data\n" +
                           "• Backup transactions\n" +
                           "• Sync across devices\n\n" +
                           "🚧 Advanced data management coming soon!")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showPrivacySettings() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("🔒 Privacy & Security")
                .setMessage("🛡️ Privacy & Security features:\n\n" +
                           "• App lock with PIN/Biometric\n" +
                           "• Hide sensitive amounts\n" +
                           "• Secure data encryption\n" +
                           "• Privacy mode\n\n" +
                           "🚧 Security features coming soon!")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showExportDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("📤 Export Data")
                .setMessage("💾 Export your financial data:\n\n" +
                           "• Export to CSV format\n" +
                           "• Generate PDF reports\n" +
                           "• Share via email/cloud\n" +
                           "• Custom date ranges\n\n" +
                           "🚧 Export functionality coming soon!")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showImportDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("📥 Import Data")
                .setMessage("📂 Import financial data:\n\n" +
                           "• Import from CSV files\n" +
                           "• Restore from backup\n" +
                           "• Sync from other apps\n" +
                           "• Merge duplicate entries\n\n" +
                           "🚧 Import functionality coming soon!")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showResetDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("🔄 Reset App")
                .setMessage("⚠️ WARNING: This will permanently delete all your data!\n\n" +
                           "This action will:\n" +
                           "• Delete all transactions\n" +
                           "• Reset all settings\n" +
                           "• Clear all categories\n" +
                           "• Remove all notes\n\n" +
                           "This action cannot be undone!")
                .setPositiveButton("Reset", (dialog, which) -> {
                    performAppReset();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Performs a complete app reset - clears all data and settings
     */
    private void performAppReset() {
        try {
            // Use DataManager for comprehensive reset
            DataManager dataManager = DataManager.getInstance(this);
            dataManager.performCompleteReset();
            
            // Clear current in-memory data
            clearAllTransactions();
            
            // Reset UI to initial state
            resetUserInterface();
            
            // Show success message with restart recommendation
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("✅ Reset Complete")
                    .setMessage("App has been reset successfully!\n\n" +
                               "📱 All data has been cleared:\n" +
                               "• Transactions\n" +
                               "• Notes\n" +
                               "• Wishlist items\n" +
                               "• Settings\n" +
                               "• Statistics\n\n" +
                               "💡 Restart the app for best experience.")
                    .setPositiveButton("OK", null)
                    .show();
            
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "❌ Error during reset: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Clears all transaction data
     */
    private void clearAllTransactions() {
        if (transactions != null) {
            transactions.clear();
            if (transactionAdapter != null) {
                transactionAdapter.updateTransactions(transactions);
            }
        }
        updateFinancialSummary();
    }

    /**
     * Clears all SharedPreferences settings
     */
    private void clearAllSettings() {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
    }

    /**
     * Resets the user interface to initial state
     */
    private void resetUserInterface() {
        // Force reload the financial summary with zeroed values
        updateFinancialSummary();
        
        // Reset to Daily tab
        if (binding.tabLayout.getTabAt(0) != null) {
            binding.tabLayout.getTabAt(0).select();
        }
        showDailyView();
        
        // Reset date to current date
        binding.currentDate.setText(dateFormat.format(new Date()));
        
        // Clear any fragments
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new Fragment())
                .commit();
        
        // Reset bottom navigation to first item
        binding.bottomNavigationView2.setSelectedItemId(R.id.transactions);
    }

    /**
     * Saves a setting to SharedPreferences
     */
    private void saveSetting(String key, String value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    /**
     * Gets a setting from SharedPreferences
     */
    private String getSetting(String key, String defaultValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, defaultValue);
        }
        return defaultValue;
    }

    /**
     * Saves a boolean setting to SharedPreferences
     */
    private void saveBooleanSetting(String key, boolean value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    /**
     * Gets a boolean setting from SharedPreferences
     */
    private boolean getBooleanSetting(String key, boolean defaultValue) {
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    private void showNavigationMenu() {
        String[] options = {
            "🏠 Go to Daily View",
            "📊 View Statistics", 
            "📝 Quick Note",
            "⚙️ Settings",
            "ℹ️ About App"
        };
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("📱 Navigation Menu")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Go to Daily View
                            binding.tabLayout.getTabAt(0).select();
                            showDailyView();
                            break;
                        case 1: // View Statistics
                            Intent statsIntent = new Intent(MainActivity.this, StatsActivity.class);
                            startActivity(statsIntent);
                            break;
                        case 2: // Quick Note
                            binding.tabLayout.getTabAt(2).select();
                            showNotesView();
                            break;
                        case 3: // Settings
                            showSettingsDialog();
                            break;
                        case 4: // About App
                            showAboutDialog();
                            break;
                    }
                })
                .show();
    }



    private void showAboutDialog() {
        String developerName = getString(R.string.developer_name);
        String developerContact = getString(R.string.developer_contact);
        String appVersion = getString(R.string.app_version);
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("ℹ️ About eManager")
                .setMessage("💰 eManager v" + appVersion + "\n\n" +
                           "A modern expense tracking app with:\n" +
                           "• 📊 Beautiful analytics\n" +
                           "• 📝 Personal notes\n" +
                           "• 📅 Calendar view\n" +
                           "• 🎯 Financial goals\n" +
                           "• 💳 Account management\n" +
                           "• 💰 Income tracking\n\n" +
                           "Built with ❤️ for better financial management\n\n" +
                           "👨‍💻 Developer:\n" +
                           "Built by " + developerName + "\n" +
                           "📞 Contact: " + developerContact)
                .setPositiveButton("OK", null)
                .show();
    }



    private void loadSampleData() {
        // Check if data has been reset
        DataManager dataManager = DataManager.getInstance(this);
        if (dataManager.isFirstRunAfterReset()) {
            // Don't load sample data if app was reset
            transactions.clear();
            transactionAdapter.updateTransactions(transactions);
            return;
        }
        
        // Clear existing transactions
        transactions.clear();
        
        // Create sample transactions with different dates
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        
        // Today's transactions
        Date today = calendar.getTime();
        transactions.add(new Transaction("1", "Salary", 50000.0, "income", "Bank", today, "Monthly salary"));
        transactions.add(new Transaction("2", "Groceries", 2500.0, "expense", "Cash", today, "Weekly groceries"));
        
        // Yesterday's transactions
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -1);
        Date yesterday = calendar.getTime();
        transactions.add(new Transaction("3", "Fuel", 3000.0, "expense", "Card", yesterday, "Car fuel"));
        
        // Transactions from 2 days ago
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -1);
        Date twoDaysAgo = calendar.getTime();
        transactions.add(new Transaction("4", "Freelance", 15000.0, "income", "Bank", twoDaysAgo, "Project payment"));
        transactions.add(new Transaction("5", "Dinner", 1200.0, "expense", "Cash", twoDaysAgo, "Restaurant"));
        
        // Tomorrow's transactions (for planning)
        calendar.setTime(today);
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        Date tomorrow = calendar.getTime();
        transactions.add(new Transaction("6", "Rent", 12000.0, "expense", "Bank", tomorrow, "Monthly rent"));
        
        // Load transactions for current date
        loadTransactionsForDate(currentDate);
    }
    
    private void loadTransactionsForDate(Date date) {
        // Filter transactions for the selected date
        List<Transaction> filteredTransactions = new ArrayList<>();
        
        // Compare dates ignoring time component
        java.util.Calendar selectedCal = java.util.Calendar.getInstance();
        selectedCal.setTime(date);
        int selectedYear = selectedCal.get(java.util.Calendar.YEAR);
        int selectedMonth = selectedCal.get(java.util.Calendar.MONTH);
        int selectedDay = selectedCal.get(java.util.Calendar.DAY_OF_MONTH);
        
        for (Transaction transaction : transactions) {
            java.util.Calendar transactionCal = java.util.Calendar.getInstance();
            transactionCal.setTime(transaction.getDate());
            int transactionYear = transactionCal.get(java.util.Calendar.YEAR);
            int transactionMonth = transactionCal.get(java.util.Calendar.MONTH);
            int transactionDay = transactionCal.get(java.util.Calendar.DAY_OF_MONTH);
            
            if (selectedYear == transactionYear && 
                selectedMonth == transactionMonth && 
                selectedDay == transactionDay) {
                filteredTransactions.add(transaction);
            }
        }
        
        // Update the RecyclerView with filtered transactions
        transactionAdapter.updateTransactions(filteredTransactions);
        
        // Update financial summary (will use all transactions for consistency)
        updateFinancialSummary();
    }

    @Override
    public void onTransactionAdded(Transaction transaction) {
        // Get the DataManager instance
        DataManager dataManager = DataManager.getInstance(this);
        
        // Ensure the transaction date matches currentDate for proper filtering
        if (!isSameDay(transaction.getDate(), currentDate)) {
            transaction.setDate(currentDate);
        }
        
        // Add the transaction using DataManager (this will update account balances)
        dataManager.addTransaction(transaction);
        
        // Add to the master list for the UI
        transactions.add(0, transaction); // Add to beginning of list
        
        // Reload transactions for the current date to refresh the view
        loadTransactionsForDate(currentDate);
        
        // Force update the entire UI
        updateFinancialSummary();
        
        // Mark app as initialized if this is the first transaction after reset
        if (dataManager.isFirstRunAfterReset()) {
            dataManager.markInitialized();
        }
        
        // Show confirmation toast with date information
        java.text.SimpleDateFormat toastFormat = new java.text.SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
        android.widget.Toast.makeText(
            this, 
            "Transaction added for " + toastFormat.format(transaction.getDate()) + 
            (transaction.getType().equals("income") ? " (Income)" : " (Expense)"), 
            android.widget.Toast.LENGTH_SHORT
        ).show();
    }
    
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    // Get all transactions - for use by fragments
    public List<Transaction> getAllTransactions() {
        return transactions;
    }
    
    // Update financial summary for all transactions
    private void updateFinancialSummary() {
        // Get summary from DataManager to ensure it matches account section
        DataManager dataManager = DataManager.getInstance(this);
        
        // Sync account balances with transactions
        dataManager.synchronizeAccounts();
        
        // Get financial summary
        double[] summary = dataManager.getFinancialSummary();
        double totalIncome = summary[0];
        double totalExpense = summary[1];
        double total = summary[2];
        
        // Get current currency symbol from settings
        String currencySymbol = getSetting("currency_symbol", "₹");

        binding.incomeValue.setText(String.format("%s%.2f", currencySymbol, totalIncome));
        binding.expenseValue.setText(String.format("%s%.2f", currencySymbol, totalExpense));
        binding.totalValue.setText(String.format("%s%.2f", currencySymbol, total));

        // Set color for total based on positive/negative
        if (total >= 0) {
            binding.totalValue.setTextColor(getColor(R.color.green));
        } else {
            binding.totalValue.setTextColor(getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            showSettingsDialog();
            return true;
        } else if (itemId == R.id.action_refresh) {
            refreshAllData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Refresh all data from DataManager and update UI
     */
    private void refreshAllData() {
        // Show a refreshing indicator
        android.widget.Toast.makeText(this, "🔄 Refreshing data...", android.widget.Toast.LENGTH_SHORT).show();
        
        // Get the DataManager instance
        DataManager dataManager = DataManager.getInstance(this);
        
        // Force synchronization of accounts with transactions
        dataManager.synchronizeAccounts();
        
        // Update the transactions list
        transactions.clear();
        transactions.addAll(dataManager.getAllTransactions());
        
        // Reload transactions for the current date
        loadTransactionsForDate(currentDate);
        
        // Update the financial summary
        updateFinancialSummary();
        
        // Show confirmation
        android.widget.Toast.makeText(this, "✅ Data refreshed successfully!", android.widget.Toast.LENGTH_SHORT).show();
    }


}
