package org.example.trackit;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.trackit.controller.ExpenseController;
import org.example.trackit.model.Category;
import org.example.trackit.model.Expense;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    private ExpenseController expenseController;
    private ObservableList<Expense> expenseData;
    private PieChart pieChart;
    private Label totalExpensesLabel;

    @Override
    public void start(Stage primaryStage) {
        expenseController = new ExpenseController();
        expenseData = FXCollections.observableArrayList(expenseController.getExpenseList());

        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);

        // Create TableView
        TableView<Expense> tableView = new TableView<>();
        tableView.setPlaceholder(new Label("No expenses added yet"));

        TableColumn<Expense, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setMinWidth(200);

        TableColumn<Expense, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setMinWidth(100);

        TableColumn<Expense, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().getName()));
        categoryCol.setMinWidth(100);

        TableColumn<Expense, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        dateCol.setMinWidth(100);

        tableView.getColumns().addAll(descriptionCol, amountCol, categoryCol, dateCol);
        tableView.setItems(expenseData);

        // Create Input Fields
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        ComboBox<Category> categoryComboBox = new ComboBox<>();
        categoryComboBox.setPromptText("Select Category");
        categoryComboBox.getItems().addAll(expenseController.getCategoryList());

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Date");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(new Label("Description:"), 0, 0);
        gridPane.add(descriptionField, 1, 0);
        gridPane.add(new Label("Amount:"), 0, 1);
        gridPane.add(amountField, 1, 1);
        gridPane.add(new Label("Category:"), 0, 2);
        gridPane.add(categoryComboBox, 1, 2);
        gridPane.add(new Label("Date:"), 0, 3);
        gridPane.add(datePicker, 1, 3);

        // Create Buttons
        Button addButton = new Button("Add Expense");
        Button removeButton = new Button("Remove Selected");
        Button editButton = new Button("Edit Selected");

        // Create Total Expenses Label
        totalExpensesLabel = new Label("Total Expenses for Current Month: $0.00");

        // Create Pie Chart
        pieChart = new PieChart();
        updatePieChart();

        addButton.setOnAction(e -> {
            expenseController.addExpense(
                    descriptionField.getText(),
                    amountField.getText(),
                    categoryComboBox.getValue(),
                    datePicker.getValue()
            );
            updateExpenseData();
            updatePieChart();
            clearFields(descriptionField, amountField, categoryComboBox, datePicker);
        });

        removeButton.setOnAction(e -> {
            Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();
            if (selectedExpense != null) {
                expenseController.removeExpense(selectedExpense);
                updateExpenseData();
                updatePieChart();
            }
        });

        editButton.setOnAction(e -> {
            Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();
            if (selectedExpense != null) {
                expenseController.editExpense(
                        selectedExpense,
                        descriptionField.getText(),
                        amountField.getText(),
                        categoryComboBox.getValue(),
                        datePicker.getValue()
                );
                updateExpenseData();
                updatePieChart();
                clearFields(descriptionField, amountField, categoryComboBox, datePicker);
            }
        });

        HBox buttonsBox = new HBox(10, addButton, removeButton, editButton);
        root.getChildren().addAll(gridPane, tableView, buttonsBox, totalExpensesLabel, pieChart);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("Expense Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateExpenseData() {
        expenseData.setAll(expenseController.getExpenseList());
        updateTotalExpensesLabel();
    }

    private void clearFields(TextField descriptionField, TextField amountField, ComboBox<Category> categoryComboBox, DatePicker datePicker) {
        descriptionField.clear();
        amountField.clear();
        categoryComboBox.setValue(null);
        datePicker.setValue(null);
    }

    private void updatePieChart() {
        Map<String, Double> categoryExpenses = new HashMap<>();
        for (Expense expense : expenseData) {
            if (expense.getDate().getMonth() == LocalDate.now().getMonth()) {
                categoryExpenses.merge(expense.getCategory().getName(), expense.getAmount(), Double::sum);
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        pieChart.setData(pieChartData);
    }

    private void updateTotalExpensesLabel() {
        double total = expenseData.stream()
                .filter(expense -> expense.getDate().getMonth() == LocalDate.now().getMonth())
                .mapToDouble(Expense::getAmount)
                .sum();
        totalExpensesLabel.setText(String.format("Total Expenses for Current Month: $%.2f", total));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
