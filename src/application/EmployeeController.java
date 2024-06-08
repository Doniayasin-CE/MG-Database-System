package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

//import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
//import java.util.ResourceBundle;

public class EmployeeController {

    @FXML
    private TextField employeeIDField, employeeNameField, salaryField;
    @FXML
    private DatePicker dateOfEmploymentPicker;
    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, Integer> employeeIDColumn;
    @FXML
    private TableColumn<Employee, String> employeeNameColumn;
    @FXML
    private TableColumn<Employee, Double> salaryColumn;
    @FXML
    private TableColumn<Employee, LocalDate> dateOfEmploymentColumn;

    /**
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeEmployeeTable();
        showEmployees();
    }*/

    public void initializeEmployeeTable(TableView<Employee> employeeTable, TableColumn<Employee, Integer> employeeIDColumn, TableColumn<Employee, String> employeeNameColumn, TableColumn<Employee, Double> salaryColumn, TableColumn<Employee, LocalDate> dateOfEmploymentColumn) {
        employeeIDColumn.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        dateOfEmploymentColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfEmployment"));
    }

    public ObservableList<Employee> getEmployees() {
        ObservableList<Employee> employeeList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Employees";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee employee = new Employee(rs.getInt("employeeID"), rs.getString("employeeName"),
                        rs.getDouble("salary"), rs.getDate("dateOfEmployment").toLocalDate());
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    public void showEmployees(TableView<Employee> employeeTable) {
        employeeTable.setItems(getEmployees());
    }

    //@FXML
    public void handleEmployeeRowSelection(TableView<Employee> employeeTable, TextField employeeIDField, TextField employeeNameField, TextField salaryField, DatePicker dateOfEmploymentPicker) {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            employeeIDField.setText(String.valueOf(selectedEmployee.getEmployeeID()));
            employeeNameField.setText(selectedEmployee.getEmployeeName());
            salaryField.setText(String.valueOf(selectedEmployee.getSalary()));
            dateOfEmploymentPicker.setValue(selectedEmployee.getDateOfEmployment());
        }
    }

    //@FXML
    public void addEmployee(TextField employeeIDField, TextField employeeNameField, TextField salaryField, DatePicker dateOfEmploymentPicker, TableView<Employee> employeeTable) {
        try {
            int employeeID = Integer.parseInt(employeeIDField.getText().trim());
            String employeeName = employeeNameField.getText().trim();
            double salary = Double.parseDouble(salaryField.getText().trim());
            LocalDate dateOfEmployment = dateOfEmploymentPicker.getValue();

            if (employeeIDExists(employeeID)) {
                displayAlert("Employee ID already exists.");
                return;
            }

            String query = "INSERT INTO Employees (employeeID, employeeName, salary, dateOfEmployment) VALUES (?, ?, ?, ?)";
            executeUpdate(query, employeeID, employeeName, salary, dateOfEmployment);
            displayAlert("Employee record inserted successfully.");
            showEmployees(employeeTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format. Please check the fields.");
        }
    }

    private boolean employeeIDExists(int employeeID) {
        return recordExists("SELECT COUNT(*) FROM Employees WHERE employeeID = ?", employeeID);
    }

    //@FXML
    public void updateEmployee(TextField employeeIDField, TextField employeeNameField, TextField salaryField, DatePicker dateOfEmploymentPicker, TableView<Employee> employeeTable) {
        try {
            int employeeID = Integer.parseInt(employeeIDField.getText().trim());
            String employeeName = employeeNameField.getText().trim();
            double salary = Double.parseDouble(salaryField.getText().trim());
            LocalDate dateOfEmployment = dateOfEmploymentPicker.getValue();

            String query = "UPDATE Employees SET employeeName = ?, salary = ?, dateOfEmployment = ? WHERE employeeID = ?";
            executeUpdate(query, employeeName, salary, dateOfEmployment, employeeID);
            displayAlert("Employee record updated successfully.");
            showEmployees(employeeTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format. Please check the fields.");
        }
    }

    //@FXML
    public void deleteEmployee(TextField employeeIDField, TableView<Employee> employeeTable) {
        try {
            int employeeID = Integer.parseInt(employeeIDField.getText().trim());
            String query = "DELETE FROM Employees WHERE employeeID = ?";
            executeUpdate(query, employeeID);
            displayAlert("Employee record deleted successfully.");
            showEmployees(employeeTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    private boolean recordExists(String query, Object... params) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void executeUpdate(String query, Object... params) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            displayAlert("Database error: " + e.getMessage());
        }
    }

    private void displayAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
