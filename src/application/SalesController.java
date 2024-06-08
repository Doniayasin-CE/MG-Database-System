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

public class SalesController{

    @FXML
    private TextField saleIDField, saleDescriptionField, saleCustomerIDField, saleCarIDField, saleEmployeeIDField;
    @FXML
    private TableView<Sale> salesTable;
    @FXML
    private TableColumn<Sale, Integer> saleIDColumn;
    @FXML
    private TableColumn<Sale, String> saleDescriptionColumn;
    @FXML
    private TableColumn<Sale, Integer> saleCustomerIDColumn, saleCarIDColumn, saleEmployeeIDColumn;

    /**
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeSaleTable();
        showSales();
    }*/

    public void initializeSaleTable(TableView<Sale> salesTable, TableColumn<Sale, Integer> saleIDColumn, TableColumn<Sale, String> saleDescriptionColumn, TableColumn<Sale, Integer> saleCustomerIDColumn, TableColumn<Sale, Integer> saleCarIDColumn, TableColumn<Sale, Integer> saleEmployeeIDColumn) {
        saleIDColumn.setCellValueFactory(new PropertyValueFactory<>("saleID"));
        saleDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("saleDescription"));
        saleCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        saleCarIDColumn.setCellValueFactory(new PropertyValueFactory<>("carID"));
        saleEmployeeIDColumn.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
    }

    public ObservableList<Sale> getSales() {
        ObservableList<Sale> salesList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Sales";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sale sale = new Sale(rs.getInt("saleID"), rs.getString("saleDescription"),
                        rs.getInt("customerID"), rs.getInt("carID"), rs.getInt("employeeID"));
                salesList.add(sale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salesList;
    }

    public void showSales(TableView<Sale> salesTable) {
        salesTable.setItems(getSales());
    }

    //@FXML
    public void handleSaleRowSelection(MouseEvent event, TableView<Sale> salesTable, TextField saleIDField, TextField saleDescriptionField, TextField saleCustomerIDField, TextField saleCarIDField, TextField saleEmployeeIDField) {
        Sale selectedSale = salesTable.getSelectionModel().getSelectedItem();
        if (selectedSale != null) {
            saleIDField.setText(String.valueOf(selectedSale.getSaleID()));
            saleDescriptionField.setText(selectedSale.getSaleDescription());
            saleCustomerIDField.setText(String.valueOf(selectedSale.getCustomerID()));
            saleCarIDField.setText(String.valueOf(selectedSale.getCarID()));
            saleEmployeeIDField.setText(String.valueOf(selectedSale.getEmployeeID()));
        }
    }

    //@FXML
    public void addSale(TextField saleIDField, TextField saleDescriptionField, TextField saleCustomerIDField, TextField saleCarIDField, TextField saleEmployeeIDField, TableView<Sale> salesTable) {
        try {
            int saleID = Integer.parseInt(saleIDField.getText().trim());
            String saleDescription = saleDescriptionField.getText().trim();
            int customerID = Integer.parseInt(saleCustomerIDField.getText().trim());
            int carID = Integer.parseInt(saleCarIDField.getText().trim());
            int employeeID = Integer.parseInt(saleEmployeeIDField.getText().trim());

            if (saleIDExists(saleID)) {
                displayAlert("Sale ID already exists.");
                return;
            }

            if (!customerIDExists(customerID)) {
                displayAlert("Customer ID does not exist.");
                return;
            }

            if (!carIDExists(carID)) {
                displayAlert("Car ID does not exist.");
                return;
            }

            if (carIDAlreadyAssigned(carID)) {
                displayAlert("Car ID is already assigned to another sale.");
                return;
            }

            if (!employeeIDExists(employeeID)) {
                displayAlert("Employee ID does not exist.");
                return;
            }

            String query = "INSERT INTO Sales (saleID, saleDescription, customerID, carID, employeeID) VALUES (?, ?, ?, ?, ?)";
            executeUpdate(query, saleID, saleDescription, customerID, carID, employeeID);
            displayAlert("Sale record inserted successfully.");
            showSales(salesTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    private boolean carIDAlreadyAssigned(int carID) {
        return recordExists("SELECT COUNT(*) FROM Sales WHERE carID = ?", carID);
    }

    //@FXML
    public void updateSale(TextField saleIDField, TextField saleDescriptionField, TextField saleCustomerIDField, TextField saleCarIDField, TextField saleEmployeeIDField, TableView<Sale> salesTable) {
        try {
            int saleID = Integer.parseInt(saleIDField.getText().trim());
            String saleDescription = saleDescriptionField.getText().trim();
            int customerID = Integer.parseInt(saleCustomerIDField.getText().trim());
            int carID = Integer.parseInt(saleCarIDField.getText().trim());
            int employeeID = Integer.parseInt(saleEmployeeIDField.getText().trim());

            if (!saleIDExists(saleID)) {
                displayAlert("Sale ID does not exist.");
                return;
            }

            if (!customerIDExists(customerID)) {
                displayAlert("customer ID does not exist.");
                return;
            }

            if (!carIDExists(carID)) {
                displayAlert("Car ID does not exist.");
                return;
            }

            if (carIDAlreadyAssignedForDifferentSale(carID, saleID)) {
                displayAlert("Car ID is already assigned to another sale.");
                return;
            }

            if (!employeeIDExists(employeeID)) {
                displayAlert("Employee ID does not exist.");
                return;
            }

            String query = "UPDATE Sales SET saleDescription = ?, customerID = ?, carID = ?, employeeID = ? WHERE saleID = ?";
            executeUpdate(query, saleDescription, customerID, carID, employeeID, saleID);
            displayAlert("Sale record updated successfully.");
            showSales(salesTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    private boolean carIDAlreadyAssignedForDifferentSale(int carID, int saleID) {
        return recordExists("SELECT COUNT(*) FROM Sales WHERE carID = ? AND saleID <> ?", carID, saleID);
    }

    //@FXML
    public void deleteSale(TextField saleIDField, TableView<Sale> salesTable) {
        try {
            int saleID = Integer.parseInt(saleIDField.getText().trim());

            if (!saleIDExists(saleID)) {
                displayAlert("Sale ID does not exist.");
                return;
            }

            String query = "DELETE FROM Sales WHERE saleID = ?";
            executeUpdate(query, saleID);
            displayAlert("Sale record deleted successfully.");
            showSales(salesTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    private boolean saleIDExists(int saleID) {
        return recordExists("SELECT COUNT(*) FROM Sales WHERE saleID = ?", saleID);
    }

    private boolean customerIDExists(int customerID) {
        return recordExists("SELECT COUNT(*) FROM Customers WHERE customerID = ?", customerID);
    }

    private boolean carIDExists(int carID) {
        return recordExists("SELECT COUNT(*) FROM Cars WHERE carID = ?", carID);
    }

    private boolean employeeIDExists(int employeeID) {
        return recordExists("SELECT COUNT(*) FROM Employees WHERE employeeID = ?", employeeID);
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
