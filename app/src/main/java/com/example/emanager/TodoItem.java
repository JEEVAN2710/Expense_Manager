package com.example.emanager;

import java.util.Date;

public class TodoItem {
    private String id;
    private String itemName;
    private double estimatedPrice;
    private String priority; // "high", "medium", "low"
    private Date targetDate;
    private String description;
    private boolean isCompleted;

    public TodoItem() {
        // Default constructor
    }

    public TodoItem(String id, String itemName, double estimatedPrice, String priority, Date targetDate, String description) {
        this.id = id;
        this.itemName = itemName;
        this.estimatedPrice = estimatedPrice;
        this.priority = priority;
        this.targetDate = targetDate;
        this.description = description;
        this.isCompleted = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}