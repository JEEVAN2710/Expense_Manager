package com.example.emanager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.emanager.databinding.FragmentMonthlyBinding;
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
import java.util.List;

public class MonthlyFragment extends Fragment {

    private FragmentMonthlyBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMonthlyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupCharts();
    }

    private void setupCharts() {
        setupPieChart();
        setupBarChart();
    }

    private void setupPieChart() {
        PieChart pieChart = binding.pieChart;
        
        // Sample data
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Food"));
        entries.add(new PieEntry(25f, "Transport"));
        entries.add(new PieEntry(20f, "Entertainment"));
        entries.add(new PieEntry(15f, "Others"));

        PieDataSet dataSet = new PieDataSet(entries, "Expense Categories");
        
        // Futuristic colors
        int[] colors = {
            Color.parseColor("#00E676"), // Green
            Color.parseColor("#00E5FF"), // Cyan
            Color.parseColor("#7C4DFF"), // Purple
            Color.parseColor("#FF6D00")  // Orange
        };
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
        
        // Sample monthly data
        List<BarEntry> incomeEntries = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();
        
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
        
        // Sample data for 6 months
        incomeEntries.add(new BarEntry(0f, 50000f));
        incomeEntries.add(new BarEntry(1f, 52000f));
        incomeEntries.add(new BarEntry(2f, 48000f));
        incomeEntries.add(new BarEntry(3f, 55000f));
        incomeEntries.add(new BarEntry(4f, 53000f));
        incomeEntries.add(new BarEntry(5f, 57000f));
        
        expenseEntries.add(new BarEntry(0f, 35000f));
        expenseEntries.add(new BarEntry(1f, 38000f));
        expenseEntries.add(new BarEntry(2f, 32000f));
        expenseEntries.add(new BarEntry(3f, 40000f));
        expenseEntries.add(new BarEntry(4f, 36000f));
        expenseEntries.add(new BarEntry(5f, 42000f));

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}