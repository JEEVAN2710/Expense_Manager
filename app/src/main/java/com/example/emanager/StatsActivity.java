package com.example.emanager;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.emanager.databinding.ActivityStatsBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    private ActivityStatsBinding binding;
    private boolean isDataReset = false;
    private List<Transaction> allTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkResetStatus();
        setupToolbar();
        loadTransactionData();
        setupCharts();
        updateKeyStats();
        updateGoalProgress();
    }
    
    private void loadTransactionData() {
        DataManager dataManager = DataManager.getInstance(this);
        allTransactions = dataManager.getAllTransactions();
    }
    
    private void checkResetStatus() {
        DataManager dataManager = DataManager.getInstance(this);
        isDataReset = dataManager.isFirstRunAfterReset();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Statistics & Analytics");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stats_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_refresh_stats) {
            // Show refreshing toast (clarify no data is being deleted)
            Toast.makeText(this, "🔄 Updating statistics display...", Toast.LENGTH_SHORT).show();
            
            // Refresh the UI only (no data modification)
            refreshAllData();
            
            // Show success toast
            Toast.makeText(this, "✅ Statistics view updated with latest transactions!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Refresh data when returning to the activity
        refreshAllData();
    }
    
    /**
     * Refresh statistics display without modifying any transaction data
     * This only updates the UI to reflect the current state of transactions
     */
    private void refreshAllData() {
        // Get the data manager but don't modify any stored transactions
        DataManager dataManager = DataManager.getInstance(this);
        
        // Just ensure accounts and transactions are in sync (no data is deleted)
        dataManager.synchronizeAccounts();
        
        // Reload existing transaction data references (no transactions are modified)
        checkResetStatus();
        loadTransactionData();
        
        // Update UI based on current data
        setupCharts();
        updateKeyStats();
        updateGoalProgress();
    }

    private void setupCharts() {
        setupPieChart();
        setupBarChart();
    }

    private void setupPieChart() {
        PieChart pieChart = binding.pieChart;
        
        List<PieEntry> entries = new ArrayList<>();
        
        // Use actual transaction data or show empty state
        if (isDataReset || allTransactions.isEmpty()) {
            // Show empty state or minimal data
            entries.add(new PieEntry(100f, "No Data"));
        } else {
            // Calculate category distribution from actual transactions
            Map<String, Float> categoryTotals = calculateCategoryDistribution();
            
            // Add entries for each category
            for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, isDataReset ? "No Data Available" : "Expense Categories");
        
        // Futuristic colors
        int[] colors;
        if (isDataReset) {
            colors = new int[]{Color.parseColor("#666666")}; // Gray for no data
        } else {
            colors = new int[]{
                Color.parseColor("#00E676"), // Green
                Color.parseColor("#00E5FF"), // Cyan
                Color.parseColor("#7C4DFF"), // Purple
                Color.parseColor("#FF6D00")  // Orange
            };
        }
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        
        // Styling
        pieChart.setBackgroundColor(Color.TRANSPARENT);
        pieChart.setHoleColor(Color.parseColor("#1A1A1A"));
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setDrawEntryLabels(false);
        
        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);
        
        Legend legend = pieChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(12f);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void setupBarChart() {
        BarChart barChart = binding.barChart;
        
        // Setup monthly data
        List<BarEntry> incomeEntries = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();
        
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        if (isDataReset || allTransactions.isEmpty()) {
            // Show zero data after reset or if no transactions
            for (int i = 0; i < 12; i++) {
                incomeEntries.add(new BarEntry(i, 0f));
                expenseEntries.add(new BarEntry(i, 0f));
            }
        } else {
            // Calculate monthly income and expense data from actual transactions
            float[] incomeData = new float[12];
            float[] expenseData = new float[12];
            
            // Initialize arrays
            for (int i = 0; i < 12; i++) {
                incomeData[i] = 0f;
                expenseData[i] = 0f;
            }
            
            // Get current year for filtering
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            
            // Process transactions
            for (Transaction transaction : allTransactions) {
                Calendar transactionCal = Calendar.getInstance();
                transactionCal.setTime(transaction.getDate());
                int transactionYear = transactionCal.get(Calendar.YEAR);
                int transactionMonth = transactionCal.get(Calendar.MONTH);
                
                // Only include transactions from current year
                if (transactionYear == currentYear) {
                    if ("income".equals(transaction.getType())) {
                        incomeData[transactionMonth] += (float) transaction.getAmount();
                    } else if ("expense".equals(transaction.getType())) {
                        expenseData[transactionMonth] += (float) transaction.getAmount();
                    }
                }
            }
            
            // Create bar entries
            for (int i = 0; i < 12; i++) {
                incomeEntries.add(new BarEntry(i, incomeData[i]));
                expenseEntries.add(new BarEntry(i, expenseData[i]));
            }
        }

        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(Color.parseColor("#00E676"));
        incomeDataSet.setValueTextColor(Color.WHITE);
        incomeDataSet.setValueTextSize(10f);

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expense");
        expenseDataSet.setColor(Color.parseColor("#FF6D00"));
        expenseDataSet.setValueTextColor(Color.WHITE);
        expenseDataSet.setValueTextSize(10f);

        BarData barData = new BarData(incomeDataSet, expenseDataSet);
        barData.setBarWidth(0.35f);
        
        barChart.setData(barData);
        barChart.groupBars(0f, 0.3f, 0f);
        
        // Styling
        barChart.setBackgroundColor(Color.TRANSPARENT);
        barChart.setDrawGridBackground(false);
        
        Description desc = new Description();
        desc.setText("");
        barChart.setDescription(desc);
        
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setTextSize(10f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#2A2A2A"));
        
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
        
        Legend legend = barChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(12f);
        
        barChart.animateY(1000);
        barChart.invalidate();
        
        // Show message if data was reset
        if (isDataReset) {
            showResetMessage();
        }
    }
    
    private void showResetMessage() {
        android.widget.Toast.makeText(this, 
            "📊 Statistics have been reset. Add transactions to see data here.", 
            android.widget.Toast.LENGTH_LONG).show();
    }
    
    /**
     * Calculate expense distribution by category
     */
    private Map<String, Float> calculateCategoryDistribution() {
        Map<String, Float> categoryTotals = new HashMap<>();
        float totalExpenses = 0f;
        
        // First pass: calculate total expenses and sum by category
        for (Transaction transaction : allTransactions) {
            if ("expense".equals(transaction.getType())) {
                String category = transaction.getCategory();
                float amount = (float) transaction.getAmount();
                totalExpenses += amount;
                
                // Add to category total
                if (categoryTotals.containsKey(category)) {
                    categoryTotals.put(category, categoryTotals.get(category) + amount);
                } else {
                    categoryTotals.put(category, amount);
                }
            }
        }
        
        // Second pass: convert amounts to percentages if we have expenses
        if (totalExpenses > 0) {
            Map<String, Float> percentages = new HashMap<>();
            for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
                float percentage = (entry.getValue() / totalExpenses) * 100f;
                percentages.put(entry.getKey(), percentage);
            }
            return percentages;
        } else {
            // If no expenses, return a default "No Data" entry
            Map<String, Float> defaultMap = new HashMap<>();
            defaultMap.put("No Expenses", 100f);
            return defaultMap;
        }
    }
    
    /**
     * Update key statistics based on actual transaction data
     */
    private void updateKeyStats() {
        if (isDataReset || allTransactions.isEmpty()) {
            // Show zeros if reset or no data
            binding.totalIncomeValue.setText("₹0.00");
            binding.totalExpenseValue.setText("₹0.00");
            binding.averageExpenseValue.setText("₹0.00");
            binding.savingsRateValue.setText("0%");
            return;
        }
        
        // Calculate statistics from actual transactions
        double totalIncome = 0.0;
        double totalExpense = 0.0;
        
        // Calculate current month totals
        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH);
        int currentYear = now.get(Calendar.YEAR);
        int currentDay = now.get(Calendar.DAY_OF_MONTH);
        int daysInCurrentMonth = now.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        double currentMonthIncome = 0.0;
        double currentMonthExpense = 0.0;
        
        // Earliest and latest transaction dates for current month to calculate actual date range
        Date earliestTransaction = null;
        Date latestTransaction = null;
        
        // Tracking transactions by date to calculate average daily expense properly
        Map<String, Double> dailyExpenses = new HashMap<>();
        
        for (Transaction transaction : allTransactions) {
            Calendar transactionCal = Calendar.getInstance();
            transactionCal.setTime(transaction.getDate());
            int transactionMonth = transactionCal.get(Calendar.MONTH);
            int transactionYear = transactionCal.get(Calendar.YEAR);
            int transactionDay = transactionCal.get(Calendar.DAY_OF_MONTH);
            
            // Track transaction date - create a date key in format "YYYY-MM-DD"
            String dateKey = String.format("%d-%02d-%02d", 
                transactionYear, transactionMonth + 1, transactionDay);
            
            if ("income".equals(transaction.getType())) {
                totalIncome += transaction.getAmount();
                
                // Check if transaction is from current month
                if (transactionMonth == currentMonth && transactionYear == currentYear) {
                    currentMonthIncome += transaction.getAmount();
                    
                    // Track date range for current month
                    if (earliestTransaction == null || transaction.getDate().before(earliestTransaction)) {
                        earliestTransaction = transaction.getDate();
                    }
                    if (latestTransaction == null || transaction.getDate().after(latestTransaction)) {
                        latestTransaction = transaction.getDate();
                    }
                }
            } else if ("expense".equals(transaction.getType())) {
                totalExpense += transaction.getAmount();
                
                // Check if transaction is from current month
                if (transactionMonth == currentMonth && transactionYear == currentYear) {
                    currentMonthExpense += transaction.getAmount();
                    
                    // Track daily expenses
                    if (dailyExpenses.containsKey(dateKey)) {
                        dailyExpenses.put(dateKey, dailyExpenses.get(dateKey) + transaction.getAmount());
                    } else {
                        dailyExpenses.put(dateKey, transaction.getAmount());
                    }
                    
                    // Track date range for current month
                    if (earliestTransaction == null || transaction.getDate().before(earliestTransaction)) {
                        earliestTransaction = transaction.getDate();
                    }
                    if (latestTransaction == null || transaction.getDate().after(latestTransaction)) {
                        latestTransaction = transaction.getDate();
                    }
                }
            }
        }
        
        // Calculate average daily expense based on actual days with transactions
        double averageExpense = 0.0;
        if (!dailyExpenses.isEmpty()) {
            // Option 1: Average of daily expenses (for days that have expenses)
            double totalDailyExpenses = 0.0;
            for (double dayExpense : dailyExpenses.values()) {
                totalDailyExpenses += dayExpense;
            }
            averageExpense = totalDailyExpenses / dailyExpenses.size();
        }
        
        // Calculate savings rate for current month - using total income and expenses
        double savingsRate = 0;
        if (currentMonthIncome > 0) {
            savingsRate = ((currentMonthIncome - currentMonthExpense) / currentMonthIncome) * 100;
            // Cap at 100% to handle edge cases
            savingsRate = Math.min(savingsRate, 100.0);
        }
        
        // Update UI
        binding.totalIncomeValue.setText(String.format("₹%.2f", totalIncome));
        binding.totalExpenseValue.setText(String.format("₹%.2f", totalExpense));
        binding.averageExpenseValue.setText(String.format("₹%.2f", averageExpense));
        binding.savingsRateValue.setText(String.format("%.1f%%", savingsRate));
    }
    
    /**
     * Update financial goal progress based on transaction data
     */
    private void updateGoalProgress() {
        if (isDataReset || allTransactions.isEmpty()) {
            // Show default values if reset or no data
            binding.savingsGoalProgress.setProgress(0);
            binding.expenseLimitProgress.setProgress(0);
            binding.savingsGoalText.setText("Savings Goal: 0%");
            binding.expenseLimitText.setText("Expense Limit: 0%");
            return;
        }
        
        // For this example, we'll use some default goals:
        // 1. Monthly savings goal: 30% of income
        // 2. Monthly expense limit: 70% of income
        
        // Calculate current month totals
        Calendar now = Calendar.getInstance();
        int currentMonth = now.get(Calendar.MONTH);
        int currentYear = now.get(Calendar.YEAR);
        
        double currentMonthIncome = 0.0;
        double currentMonthExpense = 0.0;
        
        for (Transaction transaction : allTransactions) {
            Calendar transactionCal = Calendar.getInstance();
            transactionCal.setTime(transaction.getDate());
            
            if (transactionCal.get(Calendar.MONTH) == currentMonth && 
                transactionCal.get(Calendar.YEAR) == currentYear) {
                if ("income".equals(transaction.getType())) {
                    currentMonthIncome += transaction.getAmount();
                } else if ("expense".equals(transaction.getType())) {
                    currentMonthExpense += transaction.getAmount();
                }
            }
        }
        
        // Calculate progress percentages
        int savingsGoalProgress = 0;
        int expenseLimitProgress = 0;
        
        if (currentMonthIncome > 0) {
            double currentSavingsRate = ((currentMonthIncome - currentMonthExpense) / currentMonthIncome) * 100;
            double expenseRate = (currentMonthExpense / currentMonthIncome) * 100;
            
            // Savings goal (target: 30%)
            savingsGoalProgress = (int) Math.min(100, (currentSavingsRate / 30) * 100);
            
            // Expense limit (target: stay under 70%)
            // 0% progress = 100% of limit used, 100% progress = 0% of limit used
            expenseLimitProgress = (int) Math.max(0, 100 - ((expenseRate / 70) * 100));
        }
        
        // Update UI
        binding.savingsGoalProgress.setProgress(savingsGoalProgress);
        binding.expenseLimitProgress.setProgress(expenseLimitProgress);
        binding.savingsGoalText.setText(String.format("Savings Goal: %d%%", savingsGoalProgress));
        binding.expenseLimitText.setText(String.format("Expense Limit: %d%%", expenseLimitProgress));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}