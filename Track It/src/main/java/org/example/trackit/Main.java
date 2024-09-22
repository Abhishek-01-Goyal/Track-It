package org.example.trackit;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.trackit.controller.ExpenseController;
import org.example.trackit.model.Category;
import org.example.trackit.model.Expense;

public class Main extends Application {

    private ExpenseController expenseController;

    @Override
    public void start(Stage primaryStage) {
        expenseController = new ExpenseController();  // Initialize the controller

        // Create a VBox to hold all the elements
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);

        // Create a TableView to display the list of expenses
        TableView<Expense> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("No expenses added yet"));

        // Add columns to the TableView
        TableColumn<Expense, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setMinWidth(200);

        TableColumn<Expense, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setMinWidth(100);

        TableColumn<Expense, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().getName())); // Update to use Category
        categoryCol.setMinWidth(100);

        TableColumn<Expense, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setMinWidth(100);

        tableView.getColumns().addAll(descriptionCol, amountCol, categoryCol, dateCol);

        // Create input fields
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        ComboBox<Category> categoryComboBox = new ComboBox<>(); // Use ComboBox for categories
        categoryComboBox.setPromptText("Select Category");
        categoryComboBox.getItems().addAll(expenseController.getCategoryList()); // Populate categories from controller

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Date");

        // Add the input fields to a GridPane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(new Label("Description:"), 0, 0);
        gridPane.add(descriptionField, 1, 0);
        gridPane.add(new Label("Amount:"), 0, 1);
        gridPane.add(amountField, 1, 1);
        gridPane.add(new Label("Category:"), 0, 2);
        gridPane.add(categoryComboBox, 1, 2); // Updated to ComboBox
        gridPane.add(new Label("Date:"), 0, 3);
        gridPane.add(datePicker, 1, 3);

        // Create buttons for adding, removing, and editing expenses
        Button addButton = new Button("Add Expense");
        Button removeButton = new Button("Remove Selected");
        Button editButton = new Button("Edit Selected");
        HBox buttonBox = new HBox(10, addButton, removeButton, editButton);

        // Create a label to display total expenses
        Label totalLabel = new Label("Total Expenses: $0.00");

        // Add all elements to the VBox layout
        root.getChildren().addAll(tableView, gridPane, buttonBox, totalLabel);

        // Add Expense button functionality
        addButton.setOnAction(e -> {
            expenseController.addExpense(
                    descriptionField.getText(),
                    amountField.getText(),
                    categoryComboBox.getValue(), // Use selected category from ComboBox
                    datePicker.getValue(),
                    tableView
            );
            updateTotalExpenses(totalLabel);
        });

        // Remove Expense button functionality
        removeButton.setOnAction(e -> {
            expenseController.removeExpense(tableView);
            updateTotalExpenses(totalLabel);
        });

        // Edit Expense button functionality
        editButton.setOnAction(e -> {
            Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();
            if (selectedExpense != null) {
                expenseController.editExpense(
                        selectedExpense,
                        descriptionField.getText(),
                        amountField.getText(),
                        categoryComboBox.getValue(), // Use selected category from ComboBox
                        datePicker.getValue(),
                        tableView
                );
                updateTotalExpenses(totalLabel);
            } else {
                showAlert("Error", "No expense selected for editing.");
            }
        });

        // Update total expenses initially
        updateTotalExpenses(totalLabel);

        // Double-click to populate fields for editing
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();
                if (selectedExpense != null) {
                    descriptionField.setText(selectedExpense.getDescription());
                    amountField.setText(String.valueOf(selectedExpense.getAmount()));
                    categoryComboBox.setValue(selectedExpense.getCategory()); // Set selected category
                    datePicker.setValue(selectedExpense.getDate());
                }
            }
        });

        // Create the scene and show the stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Expense Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to update the total expenses label
    private void updateTotalExpenses(Label totalLabel) {
        double total = expenseController.getExpenseList().stream().mapToDouble(Expense::getAmount).sum();
        totalLabel.setText(String.format("Total Expenses: $%.2f", total));
    }

    // Utility method to show an alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
