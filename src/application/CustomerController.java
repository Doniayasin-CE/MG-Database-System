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

public class CustomerController {

    @FXML
    private TextField customerIDField, customerNameField, emailField, ninField, phoneField;
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> customerIDColumn;
    @FXML
    private TableColumn<Customer, String> customerNameColumn, emailColumn, ninColumn;
    @FXML
    private TableView<CustomersPhone> phoneTable;
    @FXML
    private TableColumn<CustomersPhone, Integer> phonecustomerIDColumn;
    @FXML
    private TableColumn<CustomersPhone, Long> phoneColumn;

    /**
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeCustomerTable();
        initializePhoneTable();
        showCustomers();
        showCustomerPhones();
    }*/

    public void initializeCustomerTable(TableView<Customer> customerTable, TableColumn<Customer, Integer> customerIDColumn, TableColumn<Customer, String> customerNameColumn, TableColumn<Customer, String> emailColumn, TableColumn<Customer, String> ninColumn) {
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        ninColumn.setCellValueFactory(new PropertyValueFactory<>("NIN"));
    }

    public void initializePhoneTable(TableView<CustomersPhone> phoneTable, TableColumn<CustomersPhone, Integer> phonecustomerIDColumn, TableColumn<CustomersPhone, Long> phoneColumn) {
        phonecustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    public ObservableList<Customer> getCustomers() {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Customers";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Customer customer = new Customer(rs.getInt("customerID"), rs.getString("customerName"),
                        rs.getString("email"), rs.getString("NIN"));
                customerList.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerList;
    }

    public void showCustomers(TableView<Customer> customerTable) {
        customerTable.setItems(getCustomers());
    }

    //@FXML
    public void handleCustomerRowSelection(MouseEvent event, TableView<Customer> customerTable, TextField customerIDField, TextField customerNameField, TextField emailField, TextField ninField, TableView<CustomersPhone> phoneTable) {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            customerIDField.setText(String.valueOf(selectedCustomer.getCustomerID()));
            customerNameField.setText(selectedCustomer.getCustomerName());
            emailField.setText(selectedCustomer.getEmail());
            ninField.setText(selectedCustomer.getNIN());
            //showCustomerPhonesForCustomer(selectedCustomer.getCustomerID(), phoneTable);
        }
    }
    
    public void handlePhoneRowSelection(MouseEvent event, TableView<CustomersPhone> phoneTable, TextField customerIDField, TextField phoneField) {
        CustomersPhone selectedPhone = phoneTable.getSelectionModel().getSelectedItem();
        if (selectedPhone != null) {
            customerIDField.setText(String.valueOf(selectedPhone.getCustomerID()));
            phoneField.setText(String.valueOf(selectedPhone.getPhone()));
        }
    }
    
    //@FXML
    public void addCustomer(TextField customerIDField, TextField customerNameField, TextField emailField, TextField ninField, TableView<Customer> customerTable) {
        try {
            int customerID = Integer.parseInt(customerIDField.getText().trim());
            String customerName = customerNameField.getText().trim();
            String email = emailField.getText().trim();
            String nin = ninField.getText().trim();

            if (customerIDExists(customerID)) {
                displayAlert("Customer ID already exists.");
                return;
            }

            if (emailExists(email)) {
                displayAlert("Email already exists.");
                return;
            }

            if (ninExists(nin)) {
                displayAlert("NIN already exists.");
                return;
            }

            String query = "INSERT INTO Customers (customerID, customerName, email, NIN) VALUES (?, ?, ?, ?)";
            executeUpdate(query, customerID, customerName, email, nin);
            displayAlert("Customer record inserted successfully.");
            showCustomers(customerTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    //@FXML
    public void updateCustomer(TextField customerIDField, TextField customerNameField, TextField emailField, TextField ninField, TableView<Customer> customerTable) {
        try {
            int customerID = Integer.parseInt(customerIDField.getText().trim());
            String customerName = customerNameField.getText().trim();
            String email = emailField.getText().trim();
            String nin = ninField.getText().trim();

            if (emailExistsForAnotherCustomer(customerID, email)) {
                displayAlert("Email already exists for another customer.");
                return;
            }

            String query = "UPDATE Customers SET customerName = ?, email = ?, NIN = ? WHERE customerID = ?";
            executeUpdate(query, customerName, email, nin, customerID);
            displayAlert("Customer record updated successfully.");
            showCustomers(customerTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    //@FXML
    public void deleteCustomer(TextField customerIDField, TableView<Customer> customerTable) {
        try {
            int customerID = Integer.parseInt(customerIDField.getText().trim());
            String query = "DELETE FROM Customers WHERE customerID = ?";
            executeUpdate(query, customerID);
            displayAlert("Customer record deleted successfully.");
            showCustomers(customerTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    private boolean customerIDExists(int customerID) {
        return recordExists("SELECT COUNT(*) FROM Customers WHERE customerID = ?", customerID);
    }

    private boolean emailExists(String email) {
        return recordExists("SELECT COUNT(*) FROM Customers WHERE email = ?", email);
    }

    private boolean ninExists(String nin) {
        return recordExists("SELECT COUNT(*) FROM Customers WHERE NIN = ?", nin);
    }

    private boolean emailExistsForAnotherCustomer(int customerID, String email) {
        String query = "SELECT COUNT(*) FROM Customers WHERE email = ? AND customerID <> ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setInt(2, customerID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<CustomersPhone> getCustomerPhones(String query) {
        ObservableList<CustomersPhone> phoneList = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CustomersPhone phone = new CustomersPhone(rs.getInt("customerID"), rs.getLong("phone"));
                phoneList.add(phone);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return phoneList;
    }

    public void showCustomerPhones(TableView<CustomersPhone> phoneTable) {
        String query = "SELECT * FROM CustomersPhone";
        phoneTable.setItems(getCustomerPhones(query));
    }

    public void showCustomerPhonesForCustomer(int customerID, TableView<CustomersPhone> phoneTable) {
        String query = "SELECT * FROM CustomersPhone WHERE customerID = " + customerID;
        phoneTable.setItems(getCustomerPhones(query));
    }

    //@FXML
    public void addPhone(TextField customerIDField, TextField phoneField, TableView<CustomersPhone> phoneTable) {
        try {
            int customerID = Integer.parseInt(customerIDField.getText().trim());
            long phone = Long.parseLong(phoneField.getText().trim());

            String query = "INSERT INTO CustomersPhone (customerID, phone) VALUES (?, ?)";
            executeUpdate(query, customerID, phone);
            displayAlert("Phone record inserted successfully.");
            showCustomerPhonesForCustomer(customerID, phoneTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    //@FXML
    public void updatePhone(TextField customerIDField, TextField phoneField, TableView<CustomersPhone> phoneTable) {
        try {
            int customerID = Integer.parseInt(customerIDField.getText().trim());
            long phone = Long.parseLong(phoneField.getText().trim());

            String query = "UPDATE CustomersPhone SET phone = ? WHERE customerID = ?";
            executeUpdate(query, phone, customerID);
            displayAlert("Phone record updated successfully.");
            showCustomerPhonesForCustomer(customerID, phoneTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    //@FXML
    public void deletePhone(TextField customerIDField, TextField phoneField, TableView<CustomersPhone> phoneTable) {
        try {
            int customerID = Integer.parseInt(customerIDField.getText().trim());
            long phone = Long.parseLong(phoneField.getText().trim());
            String query = "DELETE FROM CustomersPhone WHERE customerID = ? AND phone = ?";
            executeUpdate(query, customerID, phone);
            displayAlert("Phone record deleted successfully.");
            showCustomerPhonesForCustomer(customerID, phoneTable);
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
