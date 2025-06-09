package com.example.emanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<TodoItem> todoItems;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private OnTodoItemClickListener listener;

    public interface OnTodoItemClickListener {
        void onTodoItemChecked(TodoItem item, boolean isChecked);
        void onTodoItemClick(TodoItem item);
        void onTodoItemDelete(TodoItem item, int position);
    }

    public TodoAdapter(List<TodoItem> todoItems, OnTodoItemClickListener listener) {
        this.todoItems = todoItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_todo_item, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        TodoItem todoItem = todoItems.get(position);
        
        holder.itemNameTextView.setText(todoItem.getItemName());
        holder.estimatedPriceTextView.setText(String.format("₹%.2f", todoItem.getEstimatedPrice()));
        holder.priorityTextView.setText(todoItem.getPriority().toUpperCase());
        
        if (todoItem.getTargetDate() != null) {
            holder.targetDateTextView.setText(dateFormat.format(todoItem.getTargetDate()));
        } else {
            holder.targetDateTextView.setText("No target date");
        }
        
        holder.checkBox.setChecked(todoItem.isCompleted());
        
        // Set priority color
        switch (todoItem.getPriority().toLowerCase()) {
            case "high":
                holder.priorityTextView.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
                break;
            case "medium":
                holder.priorityTextView.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_orange_dark));
                break;
            case "low":
                holder.priorityTextView.setTextColor(holder.itemView.getContext().getColor(R.color.green));
                break;
        }
        
        // Set checkbox listener
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onTodoItemChecked(todoItem, isChecked);
            }
        });
        
        // Set item click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTodoItemClick(todoItem);
            }
        });
        
        // Set delete button listener
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTodoItemDelete(todoItem, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    public void updateTodoItems(List<TodoItem> newTodoItems) {
        this.todoItems = newTodoItems;
        notifyDataSetChanged();
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView itemNameTextView;
        TextView estimatedPriceTextView;
        TextView priorityTextView;
        TextView targetDateTextView;
        ImageButton deleteButton;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.todo_checkbox);
            itemNameTextView = itemView.findViewById(R.id.todo_item_name);
            estimatedPriceTextView = itemView.findViewById(R.id.todo_estimated_price);
            priorityTextView = itemView.findViewById(R.id.todo_priority);
            targetDateTextView = itemView.findViewById(R.id.todo_target_date);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}