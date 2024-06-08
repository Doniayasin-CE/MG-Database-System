package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

//import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.ResourceBundle;

public class ManagersController{

    @FXML
    private TextField managerEmployeeIDField, monthlyReportField;
    @FXML
    private TableView<Managers> managerTable;
    @FXML
    private TableColumn<Managers, Integer> managerEmployeeIDColumn;
    @FXML
    private TableColumn<Managers, String> monthlyReportColumn;

    /**
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeManagerTable();
        showManagers();
    }*/

    public void initializeManagerTable(TableView<Managers> managerTable, TableColumn<Managers, Integer> managerEmployeeIDColumn, TableColumn<Managers, String> monthlyReportColumn) {
        managerEmployeeIDColumn.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        monthlyReportColumn.setCellValueFactory(new PropertyValueFactory<>("monthlyReport"));
    }

    public ObservableList<Managers> getManagers() {
        ObservableList<Managers> managerList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Managers";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Managers manager = new Managers(rs.getInt("employeeID"), rs.getString("MonthlyReport"));
                managerList.add(manager);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return managerList;
    }

    public void showManagers(TableView<Managers> managerTable) {
        managerTable.setItems(getManagers());
    }

    //@FXML
    public void handleManagerRowSelection(MouseEvent event, TableView<Managers> managerTable, TextField managerEmployeeIDField, TextField monthlyReportField) {
        Managers selectedManager = managerTable.getSelectionModel().getSelectedItem();
        if (selectedManager != null) {
            managerEmployeeIDField.setText(String.valueOf(selectedManager.getEmployeeID()));
            monthlyReportField.setText(selectedManager.getMonthlyReport());
        }
    }


    //@FXML
    public void addManager(TextField managerEmployeeIDField, TextField monthlyReportField, TableView<Managers> managerTable) {
        try {
            int employeeID = Integer.parseInt(managerEmployeeIDField.getText().trim());
            String monthlyReport = monthlyReportField.getText().trim();

            if (!employeeIDExistsInEmployees(employeeID)) {
                displayAlert("Employee ID does not exist in Employees table.");
                return;
            }

            if (employeeIDExistsInOthers(employeeID)) {
                displayAlert("This Employee ID is already used in Advertisers or Salesmans or Managers.");
                return;
            }

            String query = "INSERT INTO Managers (employeeID, MonthlyReport) VALUES (?, ?)";
            executeUpdate(query, employeeID, monthlyReport);
            displayAlert("Manager record inserted successfully.");
            showManagers(managerTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format. Please check the fields.");
        }
    }

    //@FXML
    public void updateManager(TextField managerEmployeeIDField, TextField monthlyReportField, TableView<Managers> managerTable) {
        try {
            int employeeID = Integer.parseInt(managerEmployeeIDField.getText().trim());
            String monthlyReport = monthlyReportField.getText().trim();

            String query = "UPDATE Managers SET MonthlyReport = ? WHERE employeeID = ?";
            executeUpdate(query, monthlyReport, employeeID);
            displayAlert("Manager record updated successfully.");
            showManagers(managerTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format. Please check the fields.");
        }
    }

    //@FXML
    public void deleteManager(TextField managerEmployeeIDField, TableView<Managers> managerTable) {
        try {
            int employeeID = Integer.parseInt(managerEmployeeIDField.getText().trim());
            String query = "DELETE FROM Managers WHERE employeeID = ?";
            executeUpdate(query, employeeID);
            displayAlert("Manager record deleted successfully.");
            showManagers(managerTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    private boolean employeeIDExistsInEmployees(int employeeID) {
        return recordExists("SELECT COUNT(*) FROM Employees WHERE employeeID = ?", employeeID);
    }

    private boolean employeeIDExistsInOthers(int employeeID) {
        String query = "SELECT COUNT(*) FROM ("
                + "SELECT employeeID FROM Managers WHERE employeeID = ? "
                + "UNION "
                + "SELECT employeeID FROM Salesmans WHERE employeeID = ? "
                + "UNION "
                + "SELECT employeeID FROM Advertisers WHERE employeeID = ?) as combined";
        return recordExists(query, employeeID, employeeID, employeeID);
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
