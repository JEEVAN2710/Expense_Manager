package com.example.emanager;

import java.util.Date;

public class Transaction {
    private String id;
    private String category;
    private double amount;
    private String type; // "income" or "expense"
    private String account;
    private Date date;
    private String description;

    public Transaction() {
        // Default constructor
    }

    public Transaction(String id, String category, double amount, String type, String account, Date date, String description) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.type = type;
        this.account = account;
        this.date = date;
        this.description = description;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}