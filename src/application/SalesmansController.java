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

public class SalesmansController{

    @FXML
    private TextField salesmanEmployeeIDField, employeeTargetField;
    @FXML
    private TableView<Salesmans> salesmanTable;
    @FXML
    private TableColumn<Salesmans, Integer> salesmanEmployeeIDColumn, employeeTargetColumn;

    /**
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeSalesmanTable();
        showSalesmans();
    }*/

    public void initializeSalesmanTable(TableView<Salesmans> salesmanTable, TableColumn<Salesmans, Integer> salesmanEmployeeIDColumn, TableColumn<Salesmans, Integer> employeeTargetColumn) {
        salesmanEmployeeIDColumn.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        employeeTargetColumn.setCellValueFactory(new PropertyValueFactory<>("employeeTarget"));
    }

    public ObservableList<Salesmans> getSalesmans() {
        ObservableList<Salesmans> salesmanList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Salesmans";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Salesmans salesman = new Salesmans(rs.getInt("employeeID"), rs.getInt("employeeTarget"));
                salesmanList.add(salesman);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salesmanList;
    }

    public void showSalesmans(TableView<Salesmans> salesmanTable) {
        salesmanTable.setItems(getSalesmans());
    }

    //@FXML
    public void handleSalesmanRowSelection(MouseEvent event, TableView<Salesmans> salesmanTable, TextField salesmanEmployeeIDField, TextField employeeTargetField) {
        Salesmans selectedSalesman = salesmanTable.getSelectionModel().getSelectedItem();
        if (selectedSalesman != null) {
            salesmanEmployeeIDField.setText(String.valueOf(selectedSalesman.getEmployeeID()));
            employeeTargetField.setText(String.valueOf(selectedSalesman.getEmployeeTarget()));
        }
    }

    //@FXML
    public void addSalesman(TextField salesmanEmployeeIDField, TextField employeeTargetField, TableView<Salesmans> salesmanTable) {
        try {
            int employeeID = Integer.parseInt(salesmanEmployeeIDField.getText().trim());
            int employeeTarget = Integer.parseInt(employeeTargetField.getText().trim());

            if (!employeeIDExistsInEmployees(employeeID)) {
                displayAlert("Employee ID does not exist in Employees table.");
                return;
            }

            if (employeeIDExistsInOthers(employeeID)) {
                displayAlert("This Employee ID is already used in Managers, Salesmans, or Advertisers.");
                return;
            }

            String query = "INSERT INTO Salesmans (employeeID, employeeTarget) VALUES (?, ?)";
            executeUpdate(query, employeeID, employeeTarget);
            displayAlert("Salesman record inserted successfully.");
            showSalesmans(salesmanTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format. Please check the fields.");
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

    //@FXML
    public void updateSalesman(TextField salesmanEmployeeIDField, TextField employeeTargetField, TableView<Salesmans> salesmanTable) {
        try {
            int employeeID = Integer.parseInt(salesmanEmployeeIDField.getText().trim());
            int employeeTarget = Integer.parseInt(employeeTargetField.getText().trim());

            String query = "UPDATE Salesmans SET employeeTarget = ? WHERE employeeID = ?";
            executeUpdate(query, employeeTarget, employeeID);
            displayAlert("Salesman record updated successfully.");
            showSalesmans(salesmanTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format. Please check the fields.");
        }
    }

    //@FXML
    public void deleteSalesman(TextField salesmanEmployeeIDField, TableView<Salesmans> salesmanTable) {
        try {
            int employeeID = Integer.parseInt(salesmanEmployeeIDField.getText().trim());
            String query = "DELETE FROM Salesmans WHERE employeeID = ?";
            executeUpdate(query, employeeID);
            displayAlert("Salesman record deleted successfully.");
            showSalesmans(salesmanTable);
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
