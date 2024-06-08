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

public class CarController{

    @FXML
    private TextField carIDField, carModelField, carTypeField, carColorField, carStatusField;
    @FXML
    private TableView<Car> carTable;
    @FXML
    private TableColumn<Car, Integer> carIDColumn;
    @FXML
    private TableColumn<Car, String> carModelColumn, carTypeColumn, carColorColumn, carStatusColumn;

    /**
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeCarTable();
        showCars();
    }*/

    public void initializeCarTable(TableView<Car> carTable, TableColumn<Car, Integer> carIDColumn, TableColumn<Car, String> carTypeColumn, TableColumn<Car, String> carModelColumn, TableColumn<Car, String> carColorColumn, TableColumn<Car, String> carStatusColumn) {
        carIDColumn.setCellValueFactory(new PropertyValueFactory<>("carID"));
        carModelColumn.setCellValueFactory(new PropertyValueFactory<>("carModel"));
        carTypeColumn.setCellValueFactory(new PropertyValueFactory<>("carType"));
        carColorColumn.setCellValueFactory(new PropertyValueFactory<>("carColor"));
        carStatusColumn.setCellValueFactory(new PropertyValueFactory<>("carStatus"));
    }

    public ObservableList<Car> getCars() {
        ObservableList<Car> carList = FXCollections.observableArrayList();
        String query = "SELECT * FROM MG_PALESTINE_BRANCH.cars";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Car car = new Car(rs.getInt("carID"), rs.getString("carType"),
                        rs.getString("carModel"), rs.getString("carColor"),
                        rs.getString("carStatus"));
                carList.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carList;
    }

    public void showCars(TableView<Car> carTable) {
        carTable.setItems(getCars());
    }

    //@FXML
    public void handleCarRowSelection(MouseEvent event, TableView<Car> carTable, TextField carIDField, TextField carModelField, TextField carTypeField, TextField carColorField, TextField carStatusField) {
        Car selectedCar = carTable.getSelectionModel().getSelectedItem();
        if (selectedCar != null) {
            carIDField.setText(String.valueOf(selectedCar.getCarID()));
            carModelField.setText(selectedCar.getCarModel());
            carTypeField.setText(selectedCar.getCarType());
            carColorField.setText(selectedCar.getCarColor());
            carStatusField.setText(selectedCar.getCarStatus());
        }
    }

    //@FXML
    public void addCar(TextField carIDField, TextField carModelField, TextField carTypeField, TextField carColorField, TextField carStatusField, TableView<Car> carTable) {
        try {
            int carID = Integer.parseInt(carIDField.getText().trim());
            String carType = carTypeField.getText().trim();
            String carModel = carModelField.getText().trim();
            String carColor = carColorField.getText().trim();
            String carStatus = carStatusField.getText().trim();

            if (carIDExists(carID)) {
                displayAlert("Car ID already exists.");
                return;
            }

            String query = "INSERT INTO MG_PALESTINE_BRANCH.cars (carID, carType, carModel, carColor, carStatus) VALUES (?, ?, ?, ?, ?)";
            executeUpdate(query, carID, carType, carModel, carColor, carStatus);
            displayAlert("Car record inserted successfully.");
            showCars(carTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    private boolean carIDExists(int carID) {
        return recordExists("SELECT COUNT(*) FROM MG_PALESTINE_BRANCH.cars WHERE carID = ?", carID);
    }

    //@FXML
    public void updateCar(TextField carIDField, TextField carModelField, TextField carTypeField, TextField carColorField, TextField carStatusField, TableView<Car> carTable) {
        try {
            int carID = Integer.parseInt(carIDField.getText().trim());
            String carType = carTypeField.getText().trim();
            String carModel = carModelField.getText().trim();
            String carColor = carColorField.getText().trim();
            String carStatus = carStatusField.getText().trim();

            String query = "UPDATE MG_PALESTINE_BRANCH.cars SET carType = ?, carModel = ?, carColor = ?, carStatus = ? WHERE carID = ?";
            executeUpdate(query, carType, carModel, carColor, carStatus, carID);
            displayAlert("Car record updated successfully.");
            showCars(carTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    //@FXML
    public void deleteCar(TextField carIDField, TableView<Car> carTable) {
        try {
            int carID = Integer.parseInt(carIDField.getText().trim());
            String query = "DELETE FROM MG_PALESTINE_BRANCH.cars WHERE carID = ?";
            executeUpdate(query, carID);
            displayAlert("Car record deleted successfully.");
            showCars(carTable);
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
