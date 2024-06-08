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

public class ServiceController {

    @FXML
    private TextField serviceIDField, customerIDField, carIDField, serviceDescriptionField;
    @FXML
    private TableView<Service> serviceTable;
    @FXML
    private TableColumn<Service, Integer> serviceIDColumn, serviceCustomerIDColumn, serviceCarIDColumn;
    @FXML
    private TableColumn<Service, String> serviceDescriptionColumn;

    /**
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeServiceTable();
        showServices();
    }*/

    public void initializeServiceTable(TableView<Service> serviceTable, TableColumn<Service, Integer> serviceIDColumn, TableColumn<Service, Integer> serviceCustomerIDColumn, TableColumn<Service, Integer> serviceCarIDColumn, TableColumn<Service, String> serviceDescriptionColumn) {
        serviceIDColumn.setCellValueFactory(new PropertyValueFactory<>("serviceID"));
        serviceCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        serviceCarIDColumn.setCellValueFactory(new PropertyValueFactory<>("carID"));
        serviceDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("serviceDescription"));
    }

    public ObservableList<Service> getServices() {
        ObservableList<Service> serviceList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Services";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Service service = new Service(rs.getInt("serviceID"), rs.getInt("customerID"),
                        rs.getInt("carID"), rs.getString("serviceDescription"));
                serviceList.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return serviceList;
    }

    public void showServices(TableView<Service> serviceTable) {
        serviceTable.setItems(getServices());
    }

    //@FXML
    public void handleServiceRowSelection(MouseEvent event, TableView<Service> serviceTable, TextField serviceIDField, TextField customerIDField, TextField carIDField, TextField serviceDescriptionField) {
        Service selectedService = serviceTable.getSelectionModel().getSelectedItem();
        if (selectedService != null) {
            serviceIDField.setText(String.valueOf(selectedService.getServiceID()));
            customerIDField.setText(String.valueOf(selectedService.getCustomerID()));
            carIDField.setText(String.valueOf(selectedService.getCarID()));
            serviceDescriptionField.setText(selectedService.getServiceDescription());
        }
    }

    //@FXML
    public void addService(TextField serviceIDField, TextField customerIDField, TextField carIDField, TextField serviceDescriptionField, TableView<Service> serviceTable) {
        try {
            int serviceID = Integer.parseInt(serviceIDField.getText().trim());
            int customerID = Integer.parseInt(customerIDField.getText().trim());
            int carID = Integer.parseInt(carIDField.getText().trim());
            String serviceDescription = serviceDescriptionField.getText().trim();

            if (serviceIDExists(serviceID)) {
                displayAlert("Service ID already exists.");
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

            String query = "INSERT INTO Services (serviceID, customerID, carID, serviceDescription) VALUES (?, ?, ?, ?)";
            executeUpdate(query, serviceID, customerID, carID, serviceDescription);
            displayAlert("Service record inserted successfully.");
            showServices(serviceTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    //@FXML
    public void updateService(TextField serviceIDField, TextField customerIDField, TextField carIDField, TextField serviceDescriptionField, TableView<Service> serviceTable) {
        try {
            int serviceID = Integer.parseInt(serviceIDField.getText().trim());
            int customerID = Integer.parseInt(customerIDField.getText().trim());
            int carID = Integer.parseInt(carIDField.getText().trim());
            String serviceDescription = serviceDescriptionField.getText().trim();

            if (!serviceIDExists(serviceID)) {
                displayAlert("Service ID does not exist.");
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

            String query = "UPDATE Services SET customerID = ?, carID = ?, serviceDescription = ? WHERE serviceID = ?";
            executeUpdate(query, customerID, carID, serviceDescription, serviceID);
            displayAlert("Service record updated successfully.");
            showServices(serviceTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    //@FXML
    public void deleteService(TextField serviceIDField, TableView<Service> serviceTable) {
        try {
            int serviceID = Integer.parseInt(serviceIDField.getText().trim());

            if (!serviceIDExists(serviceID)) {
                displayAlert("Service ID does not exist.");
                return;
            }

            String query = "DELETE FROM Services WHERE serviceID = ?";
            executeUpdate(query, serviceID);
            displayAlert("Service record deleted successfully.");
            showServices(serviceTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    private boolean serviceIDExists(int serviceID) {
        return recordExists("SELECT COUNT(*) FROM Services WHERE serviceID = ?", serviceID);
    }

    private boolean customerIDExists(int customerID) {
        return recordExists("SELECT COUNT(*) FROM Customers WHERE customerID = ?", customerID);
    }

    private boolean carIDExists(int carID) {
        return recordExists("SELECT COUNT(*) FROM Cars WHERE carID = ?", carID);
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
