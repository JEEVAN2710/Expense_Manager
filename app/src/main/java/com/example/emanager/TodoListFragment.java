package com.example.emanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.emanager.databinding.FragmentTodoListBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TodoListFragment extends Fragment implements TodoAdapter.OnTodoItemClickListener {

    private FragmentTodoListBinding binding;
    private TodoAdapter todoAdapter;
    private List<TodoItem> todoItems;

    public TodoListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTodoListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupFab();
        loadTodoItems();
    }

    private void setupRecyclerView() {
        todoItems = new ArrayList<>();
        todoAdapter = new TodoAdapter(todoItems, this);
        binding.todoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.todoRecyclerView.setAdapter(todoAdapter);
    }

    private void setupFab() {
        binding.addTodoFab.setOnClickListener(v -> {
            AddTodoFragment addTodoFragment = new AddTodoFragment();
            addTodoFragment.setOnTodoAddedListener(this::addTodoItem);
            addTodoFragment.show(getParentFragmentManager(), "AddTodoFragment");
        });
    }

    private void loadTodoItems() {
        // Check if data has been reset
        DataManager dataManager = DataManager.getInstance(requireContext());
        todoItems.clear();
        
        if (dataManager.isFirstRunAfterReset()) {
            // Don't load sample data if app was reset
            todoAdapter.updateTodoItems(todoItems);
            updateTotalEstimatedCost();
            android.widget.Toast.makeText(getContext(), 
                "🎯 Wishlist has been reset. Add new items using the + button.", 
                android.widget.Toast.LENGTH_LONG).show();
            return;
        }
        
        // Add some sample todo items (only if not reset)
        todoItems.add(new TodoItem("1", "New Laptop", 50000.0, "high", new Date(), "For work and gaming"));
        todoItems.add(new TodoItem("2", "Smartphone", 25000.0, "medium", new Date(), "Upgrade current phone"));
        todoItems.add(new TodoItem("3", "Books", 2000.0, "low", new Date(), "Programming books"));
        
        todoAdapter.updateTodoItems(todoItems);
        updateTotalEstimatedCost();
    }

    private void addTodoItem(TodoItem todoItem) {
        todoItems.add(todoItem);
        todoAdapter.updateTodoItems(todoItems);
        updateTotalEstimatedCost();
    }

    private void updateTotalEstimatedCost() {
        double totalCost = 0.0;
        for (TodoItem item : todoItems) {
            if (!item.isCompleted()) {
                totalCost += item.getEstimatedPrice();
            }
        }
        binding.totalEstimatedCost.setText(String.format("Total Estimated Cost: ₹%.2f", totalCost));
    }

    @Override
    public void onTodoItemChecked(TodoItem item, boolean isChecked) {
        item.setCompleted(isChecked);
        updateTotalEstimatedCost();
        // Here you can save the updated state to database/preferences
    }

    @Override
    public void onTodoItemClick(TodoItem item) {
        // Handle item click - maybe show details or edit
        // For now, we'll just show a simple message
    }

    @Override
    public void onTodoItemDelete(TodoItem item, int position) {
        // Show confirmation dialog before deleting
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete \"" + item.getItemName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Remove item from list
                    todoItems.remove(position);
                    todoAdapter.notifyItemRemoved(position);
                    todoAdapter.notifyItemRangeChanged(position, todoItems.size());
                    updateTotalEstimatedCost();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}