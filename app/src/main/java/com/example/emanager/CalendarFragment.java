package com.example.emanager;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.example.emanager.databinding.FragmentCalendarBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private CustomCalendarView calendarView;
    private BarChart monthlyExpenseChart;
    private Set<String> transactionDates; // Store dates as strings in format "yyyy-MM-dd"
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private List<Transaction> allTransactions; // Store all transactions
    private TransactionAdapter transactionAdapter; // Adapter for showing transactions

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize transactions list
        allTransactions = ((MainActivity) requireActivity()).getAllTransactions();
        
        // Setup the transaction RecyclerView
        setupTransactionList();
        
        // Setup other components
        setupCalendarView();
        setupMonthlyExpenseChart();
        loadTransactionDates();
        setupMonthlyExpenseData();
    }
    
    private void setupTransactionList() {
        // Initialize RecyclerView for transactions
        transactionAdapter = new TransactionAdapter(new ArrayList<>());
        binding.calendarTransactionsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.calendarTransactionsList.setAdapter(transactionAdapter);
    }

    private void setupCalendarView() {
        calendarView = binding.calendarView;
        
        // Set calendar listener
        calendarView.setOnDateSelectedListener(this::showTransactionsForDate);
        
        // Setup month navigation
        Calendar currentCalendar = Calendar.getInstance();
        updateMonthDisplay(currentCalendar);
        
        // Previous month button
        binding.prevMonthBtn.setOnClickListener(v -> {
            Calendar calendar = calendarView.getCurrentCalendar();
            calendar.add(Calendar.MONTH, -1);
            calendarView.setCurrentMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
            updateMonthDisplay(calendar);
            loadTransactionDates(); // Refresh transaction indicators
        });
        
        // Next month button
        binding.nextMonthBtn.setOnClickListener(v -> {
            Calendar calendar = calendarView.getCurrentCalendar();
            calendar.add(Calendar.MONTH, 1);
            calendarView.setCurrentMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
            updateMonthDisplay(calendar);
            loadTransactionDates(); // Refresh transaction indicators
        });
    }
    
    private void updateMonthDisplay(Calendar calendar) {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        binding.currentMonthText.setText(monthYearFormat.format(calendar.getTime()));
    }

    private void setupMonthlyExpenseChart() {
        monthlyExpenseChart = binding.monthlyExpenseChart;
        
        // Configure chart appearance
        monthlyExpenseChart.getDescription().setEnabled(false);
        monthlyExpenseChart.setDrawGridBackground(false);
        monthlyExpenseChart.setDrawBarShadow(false);
        monthlyExpenseChart.setDrawValueAboveBar(true);
        monthlyExpenseChart.setPinchZoom(false);
        monthlyExpenseChart.setDoubleTapToZoomEnabled(false);
        
        // Configure X-axis
        XAxis xAxis = monthlyExpenseChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12f);
        
        // Configure Y-axis
        YAxis leftAxis = monthlyExpenseChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.GRAY);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setTextSize(12f);
        leftAxis.setAxisMinimum(0f);
        
        YAxis rightAxis = monthlyExpenseChart.getAxisRight();
        rightAxis.setEnabled(false);
        
        // Set chart background
        monthlyExpenseChart.setBackgroundColor(Color.TRANSPARENT);
    }

    private void loadTransactionDates() {
        // Extract unique dates from actual transactions
        transactionDates = new HashSet<>();
        
        for (Transaction transaction : allTransactions) {
            transactionDates.add(dateFormat.format(transaction.getDate()));
        }
        
        // Update the custom calendar view with transaction dates
        calendarView.setTransactionDates(transactionDates);
    }

    private void setupMonthlyExpenseData() {
        // Calculate monthly expense data from actual transactions
        List<BarEntry> entries = new ArrayList<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        // Get current year
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        
        // Initialize monthly totals
        float[] expenseData = new float[12];
        for (int i = 0; i < 12; i++) {
            expenseData[i] = calculateMonthlyExpense(currentYear, i);
        }
        
        // Create bar entries from calculated data
        for (int i = 0; i < expenseData.length; i++) {
            entries.add(new BarEntry(i, expenseData[i]));
        }
        
        BarDataSet dataSet = new BarDataSet(entries, "Monthly Expenses");
        
        // Set futuristic colors
        int[] colors = {
            Color.parseColor("#00E5FF"), // Cyan
            Color.parseColor("#E91E63"), // Pink
            Color.parseColor("#9C27B0"), // Purple
            Color.parseColor("#3F51B5"), // Indigo
            Color.parseColor("#2196F3"), // Blue
            Color.parseColor("#00BCD4"), // Cyan
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#8BC34A"), // Light Green
            Color.parseColor("#CDDC39"), // Lime
            Color.parseColor("#FFEB3B"), // Yellow
            Color.parseColor("#FF9800"), // Orange
            Color.parseColor("#FF5722")  // Deep Orange
        };
        
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);
        
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f);
        
        monthlyExpenseChart.setData(barData);
        
        // Set month labels
        XAxis xAxis = monthlyExpenseChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setLabelCount(12);
        
        monthlyExpenseChart.invalidate(); // refresh chart
    }
    
    private float calculateMonthlyExpense(int year, int month) {
        float totalExpense = 0f;
        
        for (Transaction transaction : allTransactions) {
            if ("expense".equals(transaction.getType())) {
                Calendar transactionCal = Calendar.getInstance();
                transactionCal.setTime(transaction.getDate());
                
                if (transactionCal.get(Calendar.YEAR) == year && 
                    transactionCal.get(Calendar.MONTH) == month) {
                    totalExpense += (float) transaction.getAmount();
                }
            }
        }
        
        return totalExpense;
    }

    private void showTransactionsForDate(Date date) {
        // Format the date for display
        String dateString = dateFormat.format(date);
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
        
        // Filter transactions for the selected date
        List<Transaction> filteredTransactions = filterTransactionsForDate(date);
        
        // Update the UI based on whether transactions exist
        if (!filteredTransactions.isEmpty()) {
            binding.selectedDateInfo.setText("✅ Transactions for " + displayFormat.format(date));
            binding.selectedDateInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent_green));
            binding.calendarTransactionsList.setVisibility(View.VISIBLE);
        } else {
            binding.selectedDateInfo.setText("❌ No transactions for " + displayFormat.format(date));
            binding.selectedDateInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
            binding.calendarTransactionsList.setVisibility(View.GONE);
        }
        
        // Update the RecyclerView
        transactionAdapter.updateTransactions(filteredTransactions);
    }
    
    private List<Transaction> filterTransactionsForDate(Date date) {
        List<Transaction> filteredTransactions = new ArrayList<>();
        
        // Compare dates ignoring time component
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTime(date);
        int selectedYear = selectedCal.get(Calendar.YEAR);
        int selectedMonth = selectedCal.get(Calendar.MONTH);
        int selectedDay = selectedCal.get(Calendar.DAY_OF_MONTH);
        
        for (Transaction transaction : allTransactions) {
            Calendar transactionCal = Calendar.getInstance();
            transactionCal.setTime(transaction.getDate());
            int transactionYear = transactionCal.get(Calendar.YEAR);
            int transactionMonth = transactionCal.get(Calendar.MONTH);
            int transactionDay = transactionCal.get(Calendar.DAY_OF_MONTH);
            
            if (selectedYear == transactionYear && 
                selectedMonth == transactionMonth && 
                selectedDay == transactionDay) {
                filteredTransactions.add(transaction);
            }
        }
        
        return filteredTransactions;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}