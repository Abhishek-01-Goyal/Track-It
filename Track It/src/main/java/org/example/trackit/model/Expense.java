package org.example.trackit.model;

import java.time.LocalDate;

public class Expense {
    private String description;
    private double amount;
    private Category category; // Change this line
    private LocalDate date;

    public Expense(String description, double amount, Category category, LocalDate date) {
        this.description = description;
        this.amount = amount;
        this.category = category; // Update constructor
        this.date = date;
    }

    // Getters and setters

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Category getCategory() {
        return category; // Update getter
    }

    public void setCategory(Category category) {
        this.category = category; // Update setter
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
