package com.example.emanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<Account> accounts;
    private OnAccountClickListener clickListener;
    private OnAccountLongClickListener longClickListener;

    public interface OnAccountClickListener {
        void onAccountClick(Account account);
    }

    public interface OnAccountLongClickListener {
        void onAccountLongClick(Account account);
    }

    public AccountAdapter(List<Account> accounts, OnAccountClickListener clickListener, OnAccountLongClickListener longClickListener) {
        this.accounts = accounts;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = accounts.get(position);
        holder.bind(account);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public void updateAccounts(List<Account> newAccounts) {
        this.accounts = newAccounts;
        notifyDataSetChanged();
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {
        private ImageView accountTypeIcon;
        private TextView accountName;
        private TextView accountType;
        private TextView accountStatus;
        private TextView accountBalance;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            accountTypeIcon = itemView.findViewById(R.id.account_type_icon);
            accountName = itemView.findViewById(R.id.account_name);
            accountType = itemView.findViewById(R.id.account_type);
            accountStatus = itemView.findViewById(R.id.account_status);
            accountBalance = itemView.findViewById(R.id.account_balance);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onAccountClick(accounts.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && longClickListener != null) {
                    longClickListener.onAccountLongClick(accounts.get(position));
                    return true;
                }
                return false;
            });
        }

        public void bind(Account account) {
            accountName.setText(account.getName());
            accountType.setText(capitalizeFirst(account.getType()));
            accountBalance.setText(account.getFormattedBalance());
            
            // Set account status
            if (account.isActive()) {
                accountStatus.setText("Active");
                accountStatus.setTextColor(itemView.getContext().getColor(R.color.green));
            } else {
                accountStatus.setText("Inactive");
                accountStatus.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            }

            // Set account type icon
            try {
                accountTypeIcon.setImageResource(account.getTypeIcon());
            } catch (Exception e) {
                // Fallback to default icon if specific icon not found
                accountTypeIcon.setImageResource(R.drawable.account); // account.png
            }

            // Set balance color based on positive/negative
            if (account.getBalance() >= 0) {
                accountBalance.setTextColor(itemView.getContext().getColor(R.color.text_primary));
            } else {
                accountBalance.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
            }
        }

        private String capitalizeFirst(String text) {
            if (text == null || text.isEmpty()) {
                return text;
            }
            return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        }
    }
}