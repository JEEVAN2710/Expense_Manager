package com.example.emanager;

import java.util.Date;

/**
 * Account model class for managing different account types and balances
 */
public class Account {
    private String id;
    private String name;
    private String type; // "bank", "cash", "card", "savings", "investment"
    private double balance;
    private String currency;
    private Date createdDate;
    private boolean isActive;
    private String description;

    public Account() {
        // Default constructor
    }

    public Account(String id, String name, String type, double balance, String currency, Date createdDate, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.currency = currency;
        this.createdDate = createdDate;
        this.description = description;
        this.isActive = true;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Add amount to account balance
     */
    public void addToBalance(double amount) {
        this.balance += amount;
    }

    /**
     * Subtract amount from account balance
     */
    public void subtractFromBalance(double amount) {
        this.balance -= amount;
    }

    /**
     * Get account type icon resource
     */
    public int getTypeIcon() {
        switch (type.toLowerCase()) {
            case "bank":
                return R.drawable.bank;
            case "cash":
                return R.drawable.cash;
            case "card":
                return R.drawable.card;
            case "savings":
                return R.drawable.savings;
            case "investment":
                return R.drawable.investment;
            default:
                return R.drawable.account; // This refers to account.png
        }
    }

    /**
     * Get formatted balance string
     */
    public String getFormattedBalance() {
        return String.format("%s%.2f", currency != null ? currency : "₹", balance);
    }

    @Override
    public String toString() {
        return name + " (" + getFormattedBalance() + ")";
    }
}