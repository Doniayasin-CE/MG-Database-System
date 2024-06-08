package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML
    private Label availableCarsLabel, soldCarsLabel, bestSellingCarTypeLabel, bestSalesmanLabel,
            cashSalesLabel, installmentSalesLabel, mostFrequentServiceLabel, topCustomerLabel;

    @FXML
    private TextField availableCarsField, soldCarsField, bestSellingCarTypeField, bestSalesmanField,
            cashSalesField, installmentSalesField, mostFrequentServiceField, topCustomerField, ninInputField,
            carIDField, serviceDescriptionField, customerIDField;

    @FXML
    private Button searchNINButton, serviceButton;

    @FXML
    private TableView<Car> availableCarsTable;
    @FXML
    private TableColumn<Car, Integer> carIDColumn;
    @FXML
    private TableColumn<Car, String> carModelColumn, carTypeColumn, carColorColumn, carStatusColumn;

    @FXML
    private GridPane serviceGridPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fetchAvailableCars();
        fetchSoldCars();
        fetchBestSellingCarType();
        fetchBestSalesman();
        fetchCashSales();
        fetchInstallmentSales();
        fetchMostFrequentService();
        fetchTopCustomer();
        initializeAvailableCarsTable();
        showAvailableCars();
    }

    private void fetchAvailableCars() {
        String query = "SELECT COUNT(*) FROM Cars WHERE carStatus = 'available'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                availableCarsField.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchSoldCars() {
        String query = "SELECT COUNT(*) FROM Sales";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                soldCarsField.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchBestSellingCarType() {
        String query = "SELECT carType, COUNT(carType) AS typeCount FROM Sales " +
                "INNER JOIN Cars ON Sales.carID = Cars.carID " +
                "GROUP BY carType ORDER BY typeCount DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                bestSellingCarTypeField.setText(rs.getString("carType"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchBestSalesman() {
        String query = "SELECT employeeName FROM Employees " +
                "INNER JOIN Salesmans ON Employees.employeeID = Salesmans.employeeID " +
                "ORDER BY employeeTarget DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                bestSalesmanField.setText(rs.getString("employeeName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchCashSales() {
        String query = "SELECT COUNT(*) FROM Cashs";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                cashSalesField.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchInstallmentSales() {
        String query = "SELECT COUNT(*) FROM InstallmentPayments";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                installmentSalesField.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchMostFrequentService() {
        String query = "SELECT ServiceDescription, COUNT(ServiceDescription) AS serviceCount " +
                "FROM Services GROUP BY ServiceDescription " +
                "ORDER BY serviceCount DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                mostFrequentServiceField.setText(rs.getString("ServiceDescription"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchTopCustomer() {
        String query = "SELECT customerName, phone FROM Customers " +
                "INNER JOIN CustomersPhone ON Customers.customerID = CustomersPhone.customerID " +
                "INNER JOIN Sales ON Customers.customerID = Sales.customerID " +
                "GROUP BY Customers.customerID, Customers.customerName, CustomersPhone.phone " +
                "ORDER BY COUNT(Sales.saleID) DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                topCustomerField.setText(rs.getString("customerName") + " - " + rs.getString("phone"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeAvailableCarsTable() {
        carIDColumn.setCellValueFactory(new PropertyValueFactory<>("carID"));
        carModelColumn.setCellValueFactory(new PropertyValueFactory<>("carModel"));
        carTypeColumn.setCellValueFactory(new PropertyValueFactory<>("carType"));
        carColorColumn.setCellValueFactory(new PropertyValueFactory<>("carColor"));
        carStatusColumn.setCellValueFactory(new PropertyValueFactory<>("carStatus"));
    }

    public ObservableList<Car> getAvailableCars() {
        ObservableList<Car> carList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Cars WHERE carStatus = 'available'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Car car = new Car(rs.getInt("carID"), rs.getString("carType"), rs.getString("carModel"),
                        rs.getString("carColor"), rs.getString("carStatus"));
                carList.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carList;
    }

    public void showAvailableCars() {
        availableCarsTable.setItems(getAvailableCars());
    }

    @FXML
    private void handleNINSearch() {
        String nin = ninInputField.getText().trim();
        if (nin.isEmpty()) {
            displayAlert("Please enter NIN number.");
            return;
        }
        String query = "SELECT * FROM Customers WHERE NIN = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    displayAlert("Welcome, our client, go to do your service.");
                    customerIDField.setText(String.valueOf(rs.getInt("customerID")));
                    serviceGridPane.setVisible(true);
                } else {
                    displayAlert("Customer not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleService() {
        int customerID = Integer.parseInt(customerIDField.getText().trim());
        int carID = Integer.parseInt(carIDField.getText().trim());
        String serviceDescription = serviceDescriptionField.getText().trim();

        if (serviceDescription.isEmpty() || carID == 0) {
            displayAlert("Please fill in all the fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String getMaxServiceIDQuery = "SELECT IFNULL(MAX(serviceID), 0) + 1 AS newServiceID FROM Services";
            try (PreparedStatement ps = conn.prepareStatement(getMaxServiceIDQuery);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newServiceID = rs.getInt("newServiceID");

                    String insertServiceQuery = "INSERT INTO Services (serviceID, customerID, carID, serviceDescription) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertPs = conn.prepareStatement(insertServiceQuery)) {
                        insertPs.setInt(1, newServiceID);
                        insertPs.setInt(2, customerID);
                        insertPs.setInt(3, carID);
                        insertPs.setString(4, serviceDescription);
                        insertPs.executeUpdate();
                    }
                    displayAlert("Service record inserted successfully.");
                }
            }
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
