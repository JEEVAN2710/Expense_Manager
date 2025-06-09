package com.example.emanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.emanager.databinding.ActivityAccountBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AccountActivity extends AppCompatActivity {

    private ActivityAccountBinding binding;
    private AccountAdapter accountAdapter;
    private SalaryAdapter salaryAdapter;
    private List<Account> accounts;
    private List<Salary> salaries;
    private DataManager dataManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataManager = DataManager.getInstance(this);
        setupToolbar();
        setupData();
        setupRecyclerViews();
        setupTabLayout();
        setupClickListeners();
        updateTotalBalance();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Account Management");
        }
    }

    private void setupData() {
        // Get accounts from DataManager
        accounts = dataManager.getAllAccounts();
        
        // Initialize salaries list
        salaries = new ArrayList<>();
        loadSampleSalaries();
    }

    private void loadSampleSalaries() {
        // Check if data has been reset
        if (dataManager.isFirstRunAfterReset()) {
            // Don't load sample data if app was reset
            return;
        }

        // Add sample salaries
        salaries.add(new Salary("1", "Monthly Salary", 75000.0, "monthly", new Date(), "1", "salary", "Primary job salary"));
        salaries.add(new Salary("2", "Freelance Work", 15000.0, "monthly", new Date(), "1", "freelance", "Side project income"));
    }

    private void setupRecyclerViews() {
        // Setup accounts recycler view
        accountAdapter = new AccountAdapter(accounts, this::onAccountClick, this::onAccountLongClick);
        binding.accountsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.accountsRecyclerView.setAdapter(accountAdapter);

        // Setup salary recycler view
        salaryAdapter = new SalaryAdapter(salaries, this::onSalaryClick, this::onSalaryLongClick);
        binding.salaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.salaryRecyclerView.setAdapter(salaryAdapter);
    }

    private void setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    // Accounts tab
                    binding.accountsSection.setVisibility(View.VISIBLE);
                    binding.salarySection.setVisibility(View.GONE);
                } else {
                    // Salary tab
                    binding.accountsSection.setVisibility(View.GONE);
                    binding.salarySection.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupClickListeners() {
        binding.addAccountBtn.setOnClickListener(v -> showAddAccountDialog());
        binding.addSalaryBtn.setOnClickListener(v -> showAddSalaryDialog());
        binding.quickSetupBtn.setOnClickListener(v -> showQuickSalarySetup());
    }

    private void showAddAccountDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_account, null);
        
        EditText nameEdit = dialogView.findViewById(R.id.account_name_edit);
        AutoCompleteTextView typeSpinner = dialogView.findViewById(R.id.account_type_spinner);
        EditText balanceEdit = dialogView.findViewById(R.id.account_balance_edit);
        EditText descriptionEdit = dialogView.findViewById(R.id.account_description_edit);

        // Setup account type dropdown
        String[] accountTypes = {"Bank", "Cash", "Card", "Savings", "Investment"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, accountTypes);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setText("Bank", false);

        new MaterialAlertDialogBuilder(this)
                .setTitle("💳 Add New Account")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameEdit.getText().toString().trim();
                    String type = typeSpinner.getText().toString().trim().toLowerCase();
                    String balanceStr = balanceEdit.getText().toString().trim();
                    String description = descriptionEdit.getText().toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(this, "Please enter account name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double balance = 0.0;
                    try {
                        balance = Double.parseDouble(balanceStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter valid balance", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    addAccount(name, type, balance, description);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddSalaryDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_salary, null);
        
        EditText titleEdit = dialogView.findViewById(R.id.salary_title_edit);
        EditText amountEdit = dialogView.findViewById(R.id.salary_amount_edit);
        AutoCompleteTextView frequencySpinner = dialogView.findViewById(R.id.salary_frequency_spinner);
        AutoCompleteTextView categorySpinner = dialogView.findViewById(R.id.salary_category_spinner);
        AutoCompleteTextView accountSpinner = dialogView.findViewById(R.id.salary_account_spinner);
        EditText descriptionEdit = dialogView.findViewById(R.id.salary_description_edit);

        // Setup dropdowns
        String[] frequencies = {"Monthly", "Weekly", "Bi-weekly", "Yearly"};
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, frequencies);
        frequencySpinner.setAdapter(frequencyAdapter);
        frequencySpinner.setText("Monthly", false);

        String[] categories = {"Salary", "Freelance", "Bonus", "Investment", "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, categories);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setText("Salary", false);

        // Setup account dropdown
        List<String> accountNames = new ArrayList<>();
        for (Account account : accounts) {
            accountNames.add(account.getName());
        }
        ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, accountNames);
        accountSpinner.setAdapter(accountAdapter);
        if (!accountNames.isEmpty()) {
            accountSpinner.setText(accountNames.get(0), false);
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("💰 Add Income Source")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = titleEdit.getText().toString().trim();
                    String amountStr = amountEdit.getText().toString().trim();
                    String frequency = frequencySpinner.getText().toString().trim().toLowerCase();
                    String category = categorySpinner.getText().toString().trim().toLowerCase();
                    String accountName = accountSpinner.getText().toString().trim();
                    String description = descriptionEdit.getText().toString().trim();

                    if (title.isEmpty()) {
                        Toast.makeText(this, "Please enter income title", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double amount = 0.0;
                    try {
                        amount = Double.parseDouble(amountStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Find account ID
                    String accountId = "1"; // Default
                    for (Account account : accounts) {
                        if (account.getName().equals(accountName)) {
                            accountId = account.getId();
                            break;
                        }
                    }

                    addSalary(title, amount, frequency, category, accountId, description);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showQuickSalarySetup() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_salary, null);
        
        EditText titleEdit = dialogView.findViewById(R.id.salary_title_edit);
        EditText amountEdit = dialogView.findViewById(R.id.salary_amount_edit);
        AutoCompleteTextView frequencySpinner = dialogView.findViewById(R.id.salary_frequency_spinner);
        AutoCompleteTextView categorySpinner = dialogView.findViewById(R.id.salary_category_spinner);
        AutoCompleteTextView accountSpinner = dialogView.findViewById(R.id.salary_account_spinner);

        // Pre-fill with common values
        titleEdit.setText("Monthly Salary");
        
        String[] frequencies = {"Monthly", "Weekly", "Bi-weekly", "Yearly"};
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, frequencies);
        frequencySpinner.setAdapter(frequencyAdapter);
        frequencySpinner.setText("Monthly", false);

        String[] categories = {"Salary", "Freelance", "Bonus", "Investment", "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, categories);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setText("Salary", false);

        // Setup account dropdown
        List<String> accountNames = new ArrayList<>();
        for (Account account : accounts) {
            accountNames.add(account.getName());
        }
        ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, accountNames);
        accountSpinner.setAdapter(accountAdapter);
        if (!accountNames.isEmpty()) {
            accountSpinner.setText(accountNames.get(0), false);
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("🚀 Quick Salary Setup")
                .setMessage("Set up your primary salary to get started with income tracking!")
                .setView(dialogView)
                .setPositiveButton("Setup", (dialog, which) -> {
                    String title = titleEdit.getText().toString().trim();
                    String amountStr = amountEdit.getText().toString().trim();
                    String frequency = frequencySpinner.getText().toString().trim().toLowerCase();
                    String category = categorySpinner.getText().toString().trim().toLowerCase();
                    String accountName = accountSpinner.getText().toString().trim();

                    if (title.isEmpty()) {
                        title = "Monthly Salary";
                    }

                    double amount = 0.0;
                    try {
                        amount = Double.parseDouble(amountStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter valid salary amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Find account ID
                    String accountId = "1"; // Default
                    for (Account account : accounts) {
                        if (account.getName().equals(accountName)) {
                            accountId = account.getId();
                            break;
                        }
                    }

                    addSalary(title, amount, frequency, category, accountId, "Primary salary income");
                    
                    // Hide quick setup card
                    binding.quickSetupCard.setVisibility(View.GONE);
                    
                    Toast.makeText(this, "🎉 Salary setup complete! You can now track your income.", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addAccount(String name, String type, double balance, String description) {
        String id = UUID.randomUUID().toString();
        Account account = new Account(id, name, type, balance, "₹", new Date(), description);
        
        // Add to DataManager for persistent storage
        dataManager.addAccount(account);
        
        // Add to local list for UI
        accounts.add(account);
        accountAdapter.notifyItemInserted(accounts.size() - 1);
        updateTotalBalance();
        
        Toast.makeText(this, "✅ Account added successfully!", Toast.LENGTH_SHORT).show();
    }

    private void addSalary(String title, double amount, String frequency, String category, String accountId, String description) {
        String id = UUID.randomUUID().toString();
        Salary salary = new Salary(id, title, amount, frequency, new Date(), accountId, category, description);
        salaries.add(salary);
        salaryAdapter.notifyItemInserted(salaries.size() - 1);
        updateTotalBalance();
        
        Toast.makeText(this, "✅ Income source added successfully!", Toast.LENGTH_SHORT).show();
    }

    private void updateTotalBalance() {
        // Use the DataManager to get the total balance to ensure consistency
        double totalBalance = dataManager.getTotalBalance();

        double monthlyIncome = 0.0;
        for (Salary salary : salaries) {
            if (salary.isActive()) {
                monthlyIncome += salary.getMonthlyAmount();
            }
        }

        binding.totalBalance.setText(String.format("₹%.2f", totalBalance));
        binding.monthlyIncome.setText(String.format("📈 Monthly Income: ₹%.2f", monthlyIncome));
    }

    private void onAccountClick(Account account) {
        // Handle account click - could show account details or transactions
        Toast.makeText(this, "Account: " + account.getName(), Toast.LENGTH_SHORT).show();
    }

    private void onAccountLongClick(Account account) {
        // Handle account long click - could show edit/delete options
        new MaterialAlertDialogBuilder(this)
                .setTitle("Account Options")
                .setMessage("What would you like to do with " + account.getName() + "?")
                .setPositiveButton("Edit", (dialog, which) -> {
                    // TODO: Implement edit account
                    Toast.makeText(this, "Edit functionality coming soon!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Delete", (dialog, which) -> {
                    accounts.remove(account);
                    accountAdapter.notifyDataSetChanged();
                    updateTotalBalance();
                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void onSalaryClick(Salary salary) {
        // Handle salary click - could show salary details
        String nextPay = dateFormat.format(salary.getNextPayDate());
        Toast.makeText(this, "Next payment: " + nextPay, Toast.LENGTH_SHORT).show();
    }

    private void onSalaryLongClick(Salary salary) {
        // Handle salary long click - show edit/delete options
        new MaterialAlertDialogBuilder(this)
                .setTitle("Income Options")
                .setMessage("What would you like to do with " + salary.getTitle() + "?")
                .setPositiveButton("Edit", (dialog, which) -> {
                    showEditSalaryDialog(salary);
                })
                .setNegativeButton("Delete", (dialog, which) -> {
                    salaries.remove(salary);
                    salaryAdapter.notifyDataSetChanged();
                    updateTotalBalance();
                    Toast.makeText(this, "Income source deleted", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Cancel", null)
                .show();
    }
    
    /**
     * Show dialog to edit an existing income source
     */
    private void showEditSalaryDialog(Salary salary) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_salary, null);
        
        EditText titleEdit = dialogView.findViewById(R.id.salary_title_edit);
        EditText amountEdit = dialogView.findViewById(R.id.salary_amount_edit);
        AutoCompleteTextView frequencySpinner = dialogView.findViewById(R.id.salary_frequency_spinner);
        AutoCompleteTextView categorySpinner = dialogView.findViewById(R.id.salary_category_spinner);
        AutoCompleteTextView accountSpinner = dialogView.findViewById(R.id.salary_account_spinner);
        EditText descriptionEdit = dialogView.findViewById(R.id.salary_description_edit);
        
        // Pre-fill fields with existing data
        titleEdit.setText(salary.getTitle());
        amountEdit.setText(String.valueOf(salary.getAmount()));
        descriptionEdit.setText(salary.getDescription());
        
        // Setup dropdowns
        String[] frequencies = {"Monthly", "Weekly", "Bi-weekly", "Yearly"};
        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, frequencies);
        frequencySpinner.setAdapter(frequencyAdapter);
        
        // Set the current frequency
        String currentFrequency = salary.getFrequencyDisplay();
        frequencySpinner.setText(currentFrequency, false);

        String[] categories = {"Salary", "Freelance", "Bonus", "Investment", "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, categories);
        categorySpinner.setAdapter(categoryAdapter);
        
        // Set the current category with proper capitalization
        String currentCategory = salary.getCategory().substring(0, 1).toUpperCase() + 
                                 salary.getCategory().substring(1).toLowerCase();
        categorySpinner.setText(currentCategory, false);

        // Setup account dropdown
        List<String> accountNames = new ArrayList<>();
        String currentAccountName = "";
        
        for (Account account : accounts) {
            accountNames.add(account.getName());
            
            // Find current account name
            if (account.getId().equals(salary.getAccountId())) {
                currentAccountName = account.getName();
            }
        }
        
        ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, accountNames);
        accountSpinner.setAdapter(accountAdapter);
        
        // Set the current account
        if (!currentAccountName.isEmpty()) {
            accountSpinner.setText(currentAccountName, false);
        } else if (!accountNames.isEmpty()) {
            accountSpinner.setText(accountNames.get(0), false);
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("✏️ Edit Income Source")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = titleEdit.getText().toString().trim();
                    String amountStr = amountEdit.getText().toString().trim();
                    String frequency = frequencySpinner.getText().toString().trim().toLowerCase();
                    String category = categorySpinner.getText().toString().trim().toLowerCase();
                    String accountName = accountSpinner.getText().toString().trim();
                    String description = descriptionEdit.getText().toString().trim();

                    if (title.isEmpty()) {
                        Toast.makeText(this, "Please enter income title", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double amount = 0.0;
                    try {
                        amount = Double.parseDouble(amountStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Find account ID
                    String accountId = salary.getAccountId(); // Default to current
                    for (Account account : accounts) {
                        if (account.getName().equals(accountName)) {
                            accountId = account.getId();
                            break;
                        }
                    }

                    // Update the salary object
                    salary.setTitle(title);
                    salary.setAmount(amount);
                    salary.setFrequency(frequency);
                    salary.setCategory(category);
                    salary.setAccountId(accountId);
                    salary.setDescription(description);
                    
                    // Update UI
                    salaryAdapter.notifyDataSetChanged();
                    updateTotalBalance();
                    
                    Toast.makeText(this, "✅ Income source updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}