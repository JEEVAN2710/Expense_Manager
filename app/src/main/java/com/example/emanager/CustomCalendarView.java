package com.example.emanager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CustomCalendarView extends GridView {
    
    private Calendar currentDate;
    private Set<String> transactionDates;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private OnDateSelectedListener onDateSelectedListener;
    private CalendarAdapter adapter;
    
    public interface OnDateSelectedListener {
        void onDateSelected(Date date);
    }
    
    public CustomCalendarView(Context context) {
        super(context);
        init();
    }
    
    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        currentDate = Calendar.getInstance();
        transactionDates = new HashSet<>();
        setNumColumns(7);
        setVerticalSpacing(8);
        setHorizontalSpacing(8);
        
        adapter = new CalendarAdapter();
        setAdapter(adapter);
        
        setOnItemClickListener((parent, view, position, id) -> {
            CalendarDay day = (CalendarDay) adapter.getItem(position);
            if (day.date != null && onDateSelectedListener != null) {
                onDateSelectedListener.onDateSelected(day.date);
            }
        });
        
        updateCalendar();
    }
    
    public void setTransactionDates(Set<String> dates) {
        this.transactionDates = dates;
        adapter.notifyDataSetChanged();
    }
    
    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.onDateSelectedListener = listener;
    }
    
    public void setCurrentMonth(int year, int month) {
        currentDate.set(Calendar.YEAR, year);
        currentDate.set(Calendar.MONTH, month);
        updateCalendar();
    }
    
    public Calendar getCurrentCalendar() {
        return (Calendar) currentDate.clone();
    }
    
    private void updateCalendar() {
        adapter.updateDays();
    }
    
    private class CalendarAdapter extends BaseAdapter {
        private List<CalendarDay> days;
        private LayoutInflater inflater;
        
        public CalendarAdapter() {
            inflater = LayoutInflater.from(getContext());
            days = new ArrayList<>();
            updateDays();
        }
        
        public void updateDays() {
            days.clear();
            
            Calendar cal = (Calendar) currentDate.clone();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            
            int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
            cal.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek);
            
            // Add 42 days (6 weeks)
            for (int i = 0; i < 42; i++) {
                CalendarDay day = new CalendarDay();
                day.date = cal.getTime();
                day.isCurrentMonth = cal.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH);
                day.hasTransaction = transactionDates.contains(dateFormat.format(day.date));
                days.add(day);
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            notifyDataSetChanged();
        }
        
        @Override
        public int getCount() {
            return days.size();
        }
        
        @Override
        public Object getItem(int position) {
            return days.get(position);
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.calendar_day_item, parent, false);
            }
            
            CalendarDay day = days.get(position);
            TextView dayText = convertView.findViewById(R.id.day_text);
            View dotIndicator = convertView.findViewById(R.id.dot_indicator);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(day.date);
            dayText.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
            
            // Set text color based on current month
            if (day.isCurrentMonth) {
                dayText.setTextColor(ContextCompat.getColor(getContext(), R.color.text_primary));
            } else {
                dayText.setTextColor(ContextCompat.getColor(getContext(), R.color.text_hint));
            }
            
            // Show dot if there are transactions
            if (day.hasTransaction) {
                dotIndicator.setVisibility(View.VISIBLE);
            } else {
                dotIndicator.setVisibility(View.GONE);
            }
            
            // Highlight today
            Calendar today = Calendar.getInstance();
            if (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                convertView.setBackgroundResource(R.drawable.calendar_today_background);
            } else {
                convertView.setBackgroundResource(R.drawable.calendar_day_background);
            }
            
            return convertView;
        }
    }
    
    private static class CalendarDay {
        Date date;
        boolean isCurrentMonth;
        boolean hasTransaction;
    }
}