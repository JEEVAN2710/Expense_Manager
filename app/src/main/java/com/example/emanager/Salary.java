package com.example.emanager;

import java.util.Date;

/**
 * Salary model class for managing salary and recurring income
 */
public class Salary {
    private String id;
    private String title;
    private double amount;
    private String frequency; // "monthly", "weekly", "bi-weekly", "yearly"
    private Date startDate;
    private Date nextPayDate;
    private String accountId; // Which account receives this salary
    private boolean isActive;
    private String description;
    private String category; // "salary", "freelance", "bonus", "investment", "other"

    public Salary() {
        // Default constructor
    }

    public Salary(String id, String title, double amount, String frequency, Date startDate, 
                  String accountId, String category, String description) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.frequency = frequency;
        this.startDate = startDate;
        this.accountId = accountId;
        this.category = category;
        this.description = description;
        this.isActive = true;
        this.nextPayDate = calculateNextPayDate();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
        this.nextPayDate = calculateNextPayDate();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        this.nextPayDate = calculateNextPayDate();
    }

    public Date getNextPayDate() {
        return nextPayDate;
    }

    public void setNextPayDate(Date nextPayDate) {
        this.nextPayDate = nextPayDate;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Calculate next pay date based on frequency
     */
    private Date calculateNextPayDate() {
        if (startDate == null || frequency == null) {
            return new Date();
        }

        long currentTime = System.currentTimeMillis();
        long startTime = startDate.getTime();
        long dayInMillis = 24 * 60 * 60 * 1000L;
        
        switch (frequency.toLowerCase()) {
            case "weekly":
                long weekInMillis = 7 * dayInMillis;
                long weeksElapsed = (currentTime - startTime) / weekInMillis;
                return new Date(startTime + (weeksElapsed + 1) * weekInMillis);
                
            case "bi-weekly":
                long biWeekInMillis = 14 * dayInMillis;
                long biWeeksElapsed = (currentTime - startTime) / biWeekInMillis;
                return new Date(startTime + (biWeeksElapsed + 1) * biWeekInMillis);
                
            case "monthly":
                // Simplified monthly calculation (30 days)
                long monthInMillis = 30 * dayInMillis;
                long monthsElapsed = (currentTime - startTime) / monthInMillis;
                return new Date(startTime + (monthsElapsed + 1) * monthInMillis);
                
            case "yearly":
                long yearInMillis = 365 * dayInMillis;
                long yearsElapsed = (currentTime - startTime) / yearInMillis;
                return new Date(startTime + (yearsElapsed + 1) * yearInMillis);
                
            default:
                return new Date(currentTime + 30 * dayInMillis); // Default to 30 days
        }
    }

    /**
     * Get monthly equivalent amount
     */
    public double getMonthlyAmount() {
        switch (frequency.toLowerCase()) {
            case "weekly":
                return amount * 4.33; // Average weeks per month
            case "bi-weekly":
                return amount * 2.17; // Average bi-weeks per month
            case "monthly":
                return amount;
            case "yearly":
                return amount / 12;
            default:
                return amount;
        }
    }

    /**
     * Get category icon resource
     */
    public int getCategoryIcon() {
        switch (category.toLowerCase()) {
            case "salary":
                return R.drawable.salary;
            case "freelance":
                return R.drawable.freelance;
            case "bonus":
                return R.drawable.bonus;
            case "investment":
                return R.drawable.investment;
            default:
                return R.drawable.income;
        }
    }

    /**
     * Get formatted amount string
     */
    public String getFormattedAmount() {
        return String.format("₹%.2f", amount);
    }

    /**
     * Get frequency display text
     */
    public String getFrequencyDisplay() {
        switch (frequency.toLowerCase()) {
            case "weekly":
                return "Weekly";
            case "bi-weekly":
                return "Bi-weekly";
            case "monthly":
                return "Monthly";
            case "yearly":
                return "Yearly";
            default:
                return frequency;
        }
    }

    @Override
    public String toString() {
        return title + " - " + getFormattedAmount() + " (" + getFrequencyDisplay() + ")";
    }
}