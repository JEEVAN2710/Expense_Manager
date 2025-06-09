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

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        
        holder.categoryTextView.setText(transaction.getCategory());
        holder.accountTextView.setText(transaction.getAccount());
        holder.dateTextView.setText(dateFormat.format(transaction.getDate()));
        
        // Format amount with currency symbol and color based on type
        String amountText = String.format("₹%.2f", transaction.getAmount());
        holder.amountTextView.setText(amountText);
        
        // Set color based on transaction type
        if ("income".equals(transaction.getType())) {
            holder.amountTextView.setTextColor(holder.itemView.getContext().getColor(R.color.green));
        } else {
            holder.amountTextView.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
        }
        
        // Set category icon (you can customize this based on category)
        // For now, using a default icon
        holder.categoryIcon.setImageResource(R.drawable.team);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void updateTransactions(List<Transaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryIcon;
        TextView categoryTextView;
        TextView accountTextView;
        TextView dateTextView;
        TextView amountTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryTextView = itemView.findViewById(R.id.transaction_Cateogery);
            accountTextView = itemView.findViewById(R.id.account_lable);
            dateTextView = itemView.findViewById(R.id.textView7);
            amountTextView = itemView.findViewById(R.id.transaction_Amount);
        }
    }
}