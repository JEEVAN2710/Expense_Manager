package com.example.emanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.emanager.databinding.FragmentAddTodoBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddTodoFragment extends BottomSheetDialogFragment {

    private FragmentAddTodoBinding binding;
    private Date selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private OnTodoAddedListener listener;

    public interface OnTodoAddedListener {
        void onTodoAdded(TodoItem todoItem);
    }

    public void setOnTodoAddedListener(OnTodoAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddTodoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupPrioritySpinner();
        setupDatePicker();
        setupButtons();
    }

    private void setupPrioritySpinner() {
        String[] priorities = {"High", "Medium", "Low"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_dropdown_item, priorities);
        binding.prioritySpinner.setAdapter(adapter);
    }

    private void setupDatePicker() {
        binding.targetDateButton.setOnClickListener(v -> showDatePicker());
        
        // Set default date to today
        selectedDate = new Date();
        binding.targetDateButton.setText(dateFormat.format(selectedDate));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDate != null) {
            calendar.setTime(selectedDate);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            getContext(),
            (view, year, month, dayOfMonth) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                selectedDate = selectedCalendar.getTime();
                binding.targetDateButton.setText(dateFormat.format(selectedDate));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setupButtons() {
        binding.addTodoButton.setOnClickListener(v -> addTodoItem());
        binding.cancelButton.setOnClickListener(v -> dismiss());
    }

    private void addTodoItem() {
        String itemName = binding.itemNameEditText.getText().toString().trim();
        String priceText = binding.estimatedPriceEditText.getText().toString().trim();
        String description = binding.descriptionEditText.getText().toString().trim();
        String priority = binding.prioritySpinner.getSelectedItem().toString().toLowerCase();

        // Validation
        if (itemName.isEmpty()) {
            binding.itemNameEditText.setError("Item name is required");
            return;
        }

        if (priceText.isEmpty()) {
            binding.estimatedPriceEditText.setError("Estimated price is required");
            return;
        }

        double estimatedPrice;
        try {
            estimatedPrice = Double.parseDouble(priceText);
            if (estimatedPrice < 0) {
                binding.estimatedPriceEditText.setError("Price cannot be negative");
                return;
            }
        } catch (NumberFormatException e) {
            binding.estimatedPriceEditText.setError("Invalid price format");
            return;
        }

        // Create TodoItem
        TodoItem todoItem = new TodoItem(
            UUID.randomUUID().toString(),
            itemName,
            estimatedPrice,
            priority,
            selectedDate,
            description
        );

        // Notify listener
        if (listener != null) {
            listener.onTodoAdded(todoItem);
        }

        Toast.makeText(getContext(), "Item added to wishlist!", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}