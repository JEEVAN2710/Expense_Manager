package com.example.emanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.SalaryViewHolder> {

    private List<Salary> salaries;
    private OnSalaryClickListener clickListener;
    private OnSalaryLongClickListener longClickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

    public interface OnSalaryClickListener {
        void onSalaryClick(Salary salary);
    }

    public interface OnSalaryLongClickListener {
        void onSalaryLongClick(Salary salary);
    }

    public SalaryAdapter(List<Salary> salaries, OnSalaryClickListener clickListener, OnSalaryLongClickListener longClickListener) {
        this.salaries = salaries;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public SalaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_salary, parent, false);
        return new SalaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalaryViewHolder holder, int position) {
        Salary salary = salaries.get(position);
        holder.bind(salary);
    }

    @Override
    public int getItemCount() {
        return salaries.size();
    }

    public void updateSalaries(List<Salary> newSalaries) {
        this.salaries = newSalaries;
        notifyDataSetChanged();
    }

    class SalaryViewHolder extends RecyclerView.ViewHolder {
        private ImageView salaryCategoryIcon;
        private TextView salaryTitle;
        private TextView salaryFrequency;
        private TextView nextPayDate;
        private TextView salaryAmountText;
        private TextView monthlyEquivalent;

        public SalaryViewHolder(@NonNull View itemView) {
            super(itemView);
            salaryCategoryIcon = itemView.findViewById(R.id.salary_category_icon);
            salaryTitle = itemView.findViewById(R.id.salary_title);
            salaryFrequency = itemView.findViewById(R.id.salary_frequency);
            nextPayDate = itemView.findViewById(R.id.next_pay_date);
            salaryAmountText = itemView.findViewById(R.id.salary_amount_text);
            monthlyEquivalent = itemView.findViewById(R.id.monthly_equivalent);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onSalaryClick(salaries.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && longClickListener != null) {
                    longClickListener.onSalaryLongClick(salaries.get(position));
                    return true;
                }
                return false;
            });
        }

        public void bind(Salary salary) {
            salaryTitle.setText(salary.getTitle());
            salaryFrequency.setText(salary.getFrequencyDisplay());
            salaryAmountText.setText(salary.getFormattedAmount());
            
            // Set next pay date
            String nextPay = "Next: " + dateFormat.format(salary.getNextPayDate());
            nextPayDate.setText(nextPay);

            // Show monthly equivalent if not monthly
            if (!salary.getFrequency().equalsIgnoreCase("monthly")) {
                monthlyEquivalent.setVisibility(View.VISIBLE);
                monthlyEquivalent.setText(String.format("₹%.0f/mo", salary.getMonthlyAmount()));
            } else {
                monthlyEquivalent.setVisibility(View.GONE);
            }

            // Set category icon
            try {
                salaryCategoryIcon.setImageResource(salary.getCategoryIcon());
            } catch (Exception e) {
                // Fallback to default icon if specific icon not found
                salaryCategoryIcon.setImageResource(R.drawable.salary);
            }

            // Set active/inactive styling
            if (salary.isActive()) {
                salaryAmountText.setTextColor(itemView.getContext().getColor(R.color.green));
                itemView.setAlpha(1.0f);
            } else {
                salaryAmountText.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
                itemView.setAlpha(0.6f);
            }
        }
    }
}