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

public class CashsController{

    @FXML
    private TextField cashSaleIDField, cashProcessNumberField, cashBankCashField, cashDirectCashField, cashDiscountsField;
    @FXML
    private TableView<Cashs> cashTable;
    @FXML
    private TableColumn<Cashs, Integer> cashSaleIDColumn, cashProcessNumberColumn;
    @FXML
    private TableColumn<Cashs, Double> cashBankCashColumn, cashDirectCashColumn;
    @FXML
    private TableColumn<Cashs, Float> cashDiscountsColumn;

    /**
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeCashTable();
        showCashs();
    }*/

    public void initializeCashTable(TableView<Cashs> cashTable, TableColumn<Cashs, Integer> cashSaleIDColumn, TableColumn<Cashs, Integer> cashProcessNumberColumn, TableColumn<Cashs, Double> cashBankCashColumn, TableColumn<Cashs, Double> cashDirectCashColumn, TableColumn<Cashs, Float> cashDiscountsColumn) {
        cashSaleIDColumn.setCellValueFactory(new PropertyValueFactory<>("saleID"));
        cashProcessNumberColumn.setCellValueFactory(new PropertyValueFactory<>("processNumber"));
        cashBankCashColumn.setCellValueFactory(new PropertyValueFactory<>("bankCash"));
        cashDirectCashColumn.setCellValueFactory(new PropertyValueFactory<>("directCash"));
        cashDiscountsColumn.setCellValueFactory(new PropertyValueFactory<>("discounts"));
    }

    public ObservableList<Cashs> getCashs() {
        ObservableList<Cashs> cashsList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Cashs";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cashs cash = new Cashs(rs.getInt("saleID"), rs.getInt("processNumber"),
                        rs.getDouble("bankCash"), rs.getDouble("directCash"),
                        rs.getFloat("discounts"));
                cashsList.add(cash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cashsList;
    }

    public void showCashs(TableView<Cashs> cashTable) {
        cashTable.setItems(getCashs());
    }

    //@FXML
    public void handleCashRowSelection(MouseEvent event, TableView<Cashs> cashTable, TextField cashSaleIDField, TextField cashProcessNumberField, TextField cashBankCashField, TextField cashDirectCashField, TextField cashDiscountsField) {
        Cashs selectedCash = cashTable.getSelectionModel().getSelectedItem();
        if (selectedCash != null) {
            cashSaleIDField.setText(String.valueOf(selectedCash.getSaleID()));
            cashProcessNumberField.setText(String.valueOf(selectedCash.getProcessNumber()));
            cashBankCashField.setText(String.valueOf(selectedCash.getBankCash()));
            cashDirectCashField.setText(String.valueOf(selectedCash.getDirectCash()));
            cashDiscountsField.setText(String.valueOf(selectedCash.getDiscounts()));
        }
    }

    //@FXML
    public void addCash(TextField cashSaleIDField, TextField cashProcessNumberField, TextField cashBankCashField, TextField cashDirectCashField, TextField cashDiscountsField, TableView<Cashs> cashTable) {
        try {
            int saleID = Integer.parseInt(cashSaleIDField.getText().trim());
            int processNumber = Integer.parseInt(cashProcessNumberField.getText().trim());
            double bankCash = Double.parseDouble(cashBankCashField.getText().trim());
            double directCash = Double.parseDouble(cashDirectCashField.getText().trim());
            float discounts = Float.parseFloat(cashDiscountsField.getText().trim());

            if (!saleIDExists(saleID)) {
                displayAlert("Sale ID does not exist.");
                return;
            }
            if (cashIDExists(saleID, processNumber)) {
                displayAlert("Sale ID and Process Number combination already exists.");
                return;
            }

            String query = "INSERT INTO Cashs (saleID, processNumber, bankCash, directCash, discounts) VALUES (?, ?, ?, ?, ?)";
            executeUpdate(query, saleID, processNumber, bankCash, directCash, discounts);
            displayAlert("Cash record inserted successfully.");
            showCashs(cashTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    //@FXML
    public void updateCash(TextField cashSaleIDField, TextField cashProcessNumberField, TextField cashBankCashField, TextField cashDirectCashField, TextField cashDiscountsField, TableView<Cashs> cashTable) {
        try {
            int saleID = Integer.parseInt(cashSaleIDField.getText().trim());
            int processNumber = Integer.parseInt(cashProcessNumberField.getText().trim());
            double bankCash = Double.parseDouble(cashBankCashField.getText().trim());
            double directCash = Double.parseDouble(cashDirectCashField.getText().trim());
            float discounts = Float.parseFloat(cashDiscountsField.getText().trim());

            String query = "UPDATE Cashs SET bankCash = ?, directCash = ?, discounts = ? WHERE saleID = ? AND processNumber = ?";
            executeUpdate(query, bankCash, directCash, discounts, saleID, processNumber);
            displayAlert("Cash record updated successfully.");
            showCashs(cashTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    //@FXML
    public void deleteCash(TextField cashSaleIDField, TextField cashProcessNumberField, TableView<Cashs> cashTable) {
        try {
            int saleID = Integer.parseInt(cashSaleIDField.getText().trim());
            int processNumber = Integer.parseInt(cashProcessNumberField.getText().trim());
            String query = "DELETE FROM Cashs WHERE saleID = ? AND processNumber = ?";
            executeUpdate(query, saleID, processNumber);
            displayAlert("Cash record deleted successfully.");
            showCashs(cashTable);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format.");
        }
    }

    private boolean saleIDExists(int saleID) {
        return recordExists("SELECT COUNT(*) FROM Sales WHERE saleID = ?", saleID);
    }

    private boolean cashIDExists(int saleID, int processNumber) {
        return recordExists("SELECT COUNT(*) FROM Cashs WHERE saleID = ? AND processNumber = ?", saleID, processNumber);
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
