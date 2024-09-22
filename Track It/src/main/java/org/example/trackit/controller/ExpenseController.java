package org.example.trackit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.example.trackit.model.Category;
import org.example.trackit.model.Expense;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ExpenseController {

    private ObservableList<Expense> expenseList;
    private ObservableList<Category> categoryList;
    private final String EXPENSE_FILE_PATH = "expenses.csv";
    private final String CATEGORY_FILE_PATH = "categories.csv";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ExpenseController() {
        expenseList = FXCollections.observableArrayList();
        categoryList = FXCollections.observableArrayList(
                new Category("Food"),
                new Category("Transport"),
                new Category("Utilities"),
                new Category("Entertainment"),
                new Category("Other")
        );
        loadExpensesFromFile();
        loadCategoriesFromFile();
    }

    public void addExpense(String description, String amountText, Category category, LocalDate date) {
        if (description.isEmpty() || amountText.isEmpty() || category == null || date == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid amount. Please enter a valid number.");
            return;
        }

        Expense newExpense = new Expense(description, amount, category, date);
        expenseList.add(newExpense);
        saveExpensesToFile();  // Save after adding
    }

    public void removeExpense(Expense selectedExpense) {
        if (selectedExpense != null) {
            expenseList.remove(selectedExpense);
            saveExpensesToFile();  // Save after removing
        } else {
            showAlert("Error", "No expense selected.");
        }
    }

    public void editExpense(Expense selectedExpense, String description, String amountText, Category category, LocalDate date) {
        if (selectedExpense == null) {
            showAlert("Error", "No expense selected.");
            return;
        }

        if (description.isEmpty() || amountText.isEmpty() || category == null || date == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            selectedExpense.setDescription(description);
            selectedExpense.setAmount(amount);
            selectedExpense.setCategory(category);
            selectedExpense.setDate(date);
            saveExpensesToFile();  // Save after editing
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid amount. Please enter a valid number.");
        }
    }

    private void saveExpensesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EXPENSE_FILE_PATH))) {
            for (Expense expense : expenseList) {
                writer.write(String.join(",",
                        expense.getDescription(),
                        String.valueOf(expense.getAmount()),
                        expense.getCategory().getName(),
                        expense.getDate().format(dateFormatter)));
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to save expenses.");
        }
    }

    private void loadExpensesFromFile() {
        File file = new File(EXPENSE_FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                String description = data[0];
                double amount = Double.parseDouble(data[1]);
                String categoryName = data[2];
                LocalDate date = LocalDate.parse(data[3], dateFormatter);

                Category category = findOrCreateCategory(categoryName);
                Expense expense = new Expense(description, amount, category, date);
                expenseList.add(expense);
            }
        } catch (FileNotFoundException e) {
            showAlert("Error", "Expense file not found.");
        } catch (Exception e) {
            showAlert("Error", "Error loading expenses.");
        }
    }

    private void loadCategoriesFromFile() {
        File file = new File(CATEGORY_FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String name = scanner.nextLine();
                Category category = new Category(name);
                categoryList.add(category);
            }
        } catch (FileNotFoundException e) {
            showAlert("Error", "Category file not found.");
        } catch (Exception e) {
            showAlert("Error", "Error loading categories.");
        }
    }

    public void addCategory(String categoryName) {
        if (categoryName.isEmpty()) {
            showAlert("Error", "Category name cannot be empty.");
            return;
        }
        Category newCategory = new Category(categoryName);
        categoryList.add(newCategory);
        saveCategoryToFile(newCategory);
    }

    private void saveCategoryToFile(Category category) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CATEGORY_FILE_PATH, true))) {
            writer.write(category.getName());
            writer.newLine();
        } catch (IOException e) {
            showAlert("Error", "Failed to save category.");
        }
    }

    private Category findOrCreateCategory(String categoryName) {
        for (Category category : categoryList) {
            if (category.getName().equals(categoryName)) {
                return category;
            }
        }
        Category newCategory = new Category(categoryName);
        categoryList.add(newCategory);
        saveCategoryToFile(newCategory);
        return newCategory;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public ObservableList<Expense> getExpenseList() {
        return expenseList;
    }

    public ObservableList<Category> getCategoryList() {
        return categoryList;
    }
}
