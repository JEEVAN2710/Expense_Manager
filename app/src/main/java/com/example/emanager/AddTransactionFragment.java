package com.example.emanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.emanager.databinding.FragmentAddTransactionBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddTransactionFragment extends BottomSheetDialogFragment {

    private FragmentAddTransactionBinding binding;
    private OnTransactionAddedListener listener;
    private Date transactionDate = new Date(); // Default to current date
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault());

    public interface OnTransactionAddedListener {
        void onTransactionAdded(Transaction transaction);
    }

    public void setOnTransactionAddedListener(OnTransactionAddedListener listener) {
        this.listener = listener;
    }
    
    public void setTransactionDate(Date date) {
        this.transactionDate = date;
        // Update calendar with the provided date
        if (date != null) {
            calendar.setTime(date);
        }
    }

    public AddTransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddTransactionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupDropdowns();
        setupDatePicker();
        setupButtons();
    }
    
    private void setupDatePicker() {
        // Set the initial date in the input field
        updateDateDisplay();
        
        // Configure date picker dialog
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            transactionDate = calendar.getTime();
            updateDateDisplay();
        };
        
        // Show date picker when clicking on the date field
        binding.dateEditText.setOnClickListener(v -> showDatePicker(dateSetListener));
        
        // Setup end icon click listener
        View endIconView = ((ViewGroup) binding.dateEditText.getParent()).findViewById(com.google.android.material.R.id.text_input_end_icon);
        if (endIconView != null) {
            endIconView.setOnClickListener(v -> showDatePicker(dateSetListener));
        }
    }
    
    private void showDatePicker(DatePickerDialog.OnDateSetListener dateSetListener) {
        new DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
    
    private void updateDateDisplay() {
        binding.dateEditText.setText(dateFormatter.format(transactionDate));
    }

    private void setupDropdowns() {
        // Setup Category Dropdown
        String[] categories = {
            "Food & Dining", "Shopping", "Transportation", "Entertainment", 
            "Bills & Utilities", "Healthcare", "Education", "Travel", 
            "Groceries", "Fuel", "Rent", "Insurance", "Investment", 
            "Salary", "Business", "Freelance", "Gift", "Other"
        };
        
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
            requireContext(), 
            R.layout.dropdown_item, 
            categories
        );
        binding.transactionCategory.setAdapter(categoryAdapter);
        
        // Setup Payment Method Dropdown
        String[] paymentMethods = {"Cash", "Card", "UPI", "Bank", "EMI"};
        
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(
            requireContext(), 
            R.layout.dropdown_item, 
            paymentMethods
        );
        binding.accountEditText.setAdapter(paymentAdapter);
        
        // Set default values
        binding.accountEditText.setText("Cash", false);
        
        // Make payment method dropdown non-editable (only selectable)
        binding.accountEditText.setKeyListener(null);
        binding.accountEditText.setOnClickListener(v -> binding.accountEditText.showDropDown());
    }

    private void setupButtons() {
        binding.submitButton.setOnClickListener(v -> addTransaction());
        binding.cancelButton.setOnClickListener(v -> dismiss());
    }

    private void addTransaction() {
        String amountText = binding.amountEditText.getText().toString().trim();
        String category = binding.transactionCategory.getText().toString().trim();
        String account = binding.accountEditText.getText().toString().trim();
        String description = binding.descriptionEditText.getText().toString().trim();

        // Validation
        if (amountText.isEmpty()) {
            binding.amountEditText.setError("Amount is required");
            return;
        }

        if (category.isEmpty()) {
            binding.transactionCategory.setError("Category is required");
            return;
        }

        if (account.isEmpty()) {
            binding.accountEditText.setError("Account is required");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                binding.amountEditText.setError("Amount must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            binding.amountEditText.setError("Invalid amount format");
            return;
        }

        // Get transaction type
        String type = binding.incomeRadio.isChecked() ? "income" : "expense";

        // Create Transaction with the specified date
        Transaction transaction = new Transaction(
            UUID.randomUUID().toString(),
            category,
            amount,
            type,
            account,
            transactionDate,
            description
        );

        // Notify listener
        if (listener != null) {
            listener.onTransactionAdded(transaction);
        }

        Toast.makeText(getContext(), "Transaction added successfully!", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
