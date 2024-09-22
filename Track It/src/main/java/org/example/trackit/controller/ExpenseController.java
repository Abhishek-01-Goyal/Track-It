package org.example.trackit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import org.example.trackit.model.Category;
import org.example.trackit.model.Expense;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ExpenseController {

    private ObservableList<Expense> expenseList;
    private ObservableList<Category> categoryList; // New line
    private final String EXPENSE_FILE_PATH = "expenses.csv";
    private final String CATEGORY_FILE_PATH = "categories.csv"; // New line
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ExpenseController() {
        expenseList = FXCollections.observableArrayList();
        categoryList = FXCollections.observableArrayList(
                new Category("Food"),
                new Category("Transport"),
                new Category("Utilities"),
                new Category("Entertainment"),
                new Category("Other")
        ); // New line
        loadExpensesFromFile();
        loadCategoriesFromFile(); // New line
    }

    // Method to add a new expense
    public void addExpense(String description, String amountText, Category category, LocalDate date, TableView<Expense> tableView) { // Updated to use Category
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
        tableView.setItems(expenseList);
    }

    // Method to remove the selected expense
    public void removeExpense(TableView<Expense> tableView) {
        Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();

        if (selectedExpense != null) {
            expenseList.remove(selectedExpense);
            saveExpensesToFile();  // Save after removing
        } else {
            showAlert("Error", "No expense selected.");
        }
    }

    // Method to edit an expense
    public void editExpense(Expense selectedExpense, String description, String amountText, Category category, LocalDate date, TableView<Expense> tableView) {
        if (selectedExpense != null) {
            removeExpense(tableView); // Remove existing expense first
            addExpense(description, amountText, category, date, tableView); // Add edited expense
        } else {
            showAlert("Error", "No expense selected.");
        }
    }

    // Method to save expenses to a CSV file
    private void saveExpensesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EXPENSE_FILE_PATH))) {
            for (Expense expense : expenseList) {
                writer.write(expense.getDescription() + "," +
                        expense.getAmount() + "," +
                        expense.getCategory().getName() + "," + // Updated to use getName()
                        expense.getDate().format(dateFormatter));
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to save expenses.");
        }
    }

    // Method to load expenses from a CSV file
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

                Category category = new Category(categoryName); // Create a new category
                Expense expense = new Expense(description, amount, category, date);
                expenseList.add(expense);
            }
        } catch (FileNotFoundException e) {
            showAlert("Error", "Expense file not found.");
        } catch (Exception e) {
            showAlert("Error", "Error loading expenses.");
        }
    }

    // Method to load categories from a CSV file
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

    // Method to add a category
    public void addCategory(String categoryName) {
        if (categoryName.isEmpty()) {
            showAlert("Error", "Category name cannot be empty.");
            return;
        }
        Category newCategory = new Category(categoryName);
        categoryList.add(newCategory);
        saveCategoryToFile(newCategory); // Save to file
    }

    // Method to save a category to a CSV file
    private void saveCategoryToFile(Category category) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CATEGORY_FILE_PATH, true))) { // Append mode
            writer.write(category.getName());
            writer.newLine();
        } catch (IOException e) {
            showAlert("Error", "Failed to save category.");
        }
    }

    // Utility method to show an alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getter for expenseList
    public ObservableList<Expense> getExpenseList() {
        return expenseList;
    }

    // Getter for categoryList
    public ObservableList<Category> getCategoryList() { // New method
        return categoryList;
    }
}
