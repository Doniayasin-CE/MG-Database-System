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

public class AdvertiserController {

    @FXML
    private TextField advertiserEmployeeIDField, pointsField;
    @FXML
    private TableView<Advertisers> advertiserTable;
    @FXML
    private TableColumn<Advertisers, Integer> advertiserEmployeeIDColumn, pointsColumn;

    /**
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeAdvertiserTable();
        showAdvertisers();
    } */

    public void initializeAdvertiserTable(TableView<Advertisers> advertiserTable, TableColumn<Advertisers, Integer> advertiserEmployeeIDColumn, TableColumn<Advertisers, Integer> pointsColumn) {
        advertiserEmployeeIDColumn.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
    }

    public ObservableList<Advertisers> getAdvertisers() {
        ObservableList<Advertisers> advertiserList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Advertisers";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Advertisers advertiser = new Advertisers(rs.getInt("employeeID"), rs.getInt("points"));
                advertiserList.add(advertiser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return advertiserList;
    }

    public void showAdvertisers(TableView<Advertisers> advertiserTable) {
        advertiserTable.setItems(getAdvertisers());
    }

    //@FXML
    public void handleAdvertiserRowSelection(MouseEvent event, TableView<Advertisers> advertiserTable, TextField advertiserEmployeeIDField, TextField pointsField) {
        Advertisers selectedAdvertiser = advertiserTable.getSelectionModel().getSelectedItem();
        if (selectedAdvertiser != null) {
            advertiserEmployeeIDField.setText(String.valueOf(selectedAdvertiser.getEmployeeID()));
            pointsField.setText(String.valueOf(selectedAdvertiser.getPoints()));
        }
    }

    //@FXML
    public void addAdvertiser(TableView<Advertisers> advertiserTable,TextField advertiserEmployeeIDField, TextField pointsField) {
        try {
            int employeeID = Integer.parseInt(advertiserEmployeeIDField.getText().trim());
            int points = Integer.parseInt(pointsField.getText().trim());

            if (!employeeIDExistsInEmployees(employeeID)) {
                displayAlert("Employee ID does not exist in Employees table.");
                return;
            }

            if (employeeIDExistsInOthers(employeeID)) {
                displayAlert("This Employee ID is already used in Managers, Salesmans, or Advertisers.");
                return;
            }

            String query = "INSERT INTO Advertisers (employeeID, points) VALUES (?, ?)";
            executeUpdate(query, employeeID, points);
            displayAlert("Advertiser record inserted successfully.");
            showAdvertisers(advertiserTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format. Please check the fields.");
        }
    }

    //@FXML
    public void updateAdvertiser(TableView<Advertisers> advertiserTable, TextField advertiserEmployeeIDField, TextField pointsField) {
        try {
            int employeeID = Integer.parseInt(advertiserEmployeeIDField.getText().trim());
            int points = Integer.parseInt(pointsField.getText().trim());

            String query = "UPDATE Advertisers SET points = ? WHERE employeeID = ?";
            executeUpdate(query, points, employeeID);
            displayAlert("Advertiser record updated successfully.");
            showAdvertisers(advertiserTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format. Please check the fields.");
        }
    }

    //@FXML
    public void deleteAdvertiser(TableView<Advertisers> advertiserTable, TextField advertiserEmployeeIDField) {
        try {
            int employeeID = Integer.parseInt(advertiserEmployeeIDField.getText().trim());
            String query = "DELETE FROM Advertisers WHERE employeeID = ?";
            executeUpdate(query, employeeID);
            displayAlert("Advertiser record deleted successfully.");
            showAdvertisers(advertiserTable);
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
