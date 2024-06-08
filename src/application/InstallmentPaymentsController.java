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

public class InstallmentPaymentsController {

    @FXML
    private TextField txt_saleID, txt_processNumber, txt_totalCost, txt_firstPayment, txt_monthlyInstallment, txt_numberOfMonths;
    @FXML
    private TableView<InstallmentPayments> tv_installmentPayments;
    @FXML
    private TableColumn<InstallmentPayments, Integer> tv_saleID, tv_processNumber;
    @FXML
    private TableColumn<InstallmentPayments, Double> tv_totalCost, tv_firstPayment, tv_monthlyInstallment;
    @FXML
    private TableColumn<InstallmentPayments, Integer> tv_numberOfMonths;

    /**
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeInstallmentPaymentsTable();
        showInstallmentPayments();
    }*/

    public void initializeInstallmentPaymentsTable(TableView<InstallmentPayments> tv_installmentPayments, TableColumn<InstallmentPayments, Integer> tv_saleID, TableColumn<InstallmentPayments, Integer> tv_processNumber, TableColumn<InstallmentPayments, Double> tv_totalCost, TableColumn<InstallmentPayments, Double> tv_firstPayment, TableColumn<InstallmentPayments, Double> tv_monthlyInstallment, TableColumn<InstallmentPayments, Integer> tv_numberOfMonths) {
        tv_saleID.setCellValueFactory(new PropertyValueFactory<>("saleID"));
        tv_processNumber.setCellValueFactory(new PropertyValueFactory<>("processNumber"));
        tv_totalCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        tv_firstPayment.setCellValueFactory(new PropertyValueFactory<>("firstPayment"));
        tv_monthlyInstallment.setCellValueFactory(new PropertyValueFactory<>("monthlyInstallment"));
        tv_numberOfMonths.setCellValueFactory(new PropertyValueFactory<>("numberOfMonths"));
    }

    public ObservableList<InstallmentPayments> getInstallmentPaymentsList() {
        ObservableList<InstallmentPayments> installmentPaymentsList = FXCollections.observableArrayList();
        String query = "SELECT * FROM InstallmentPayments";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                InstallmentPayments installmentPayments = new InstallmentPayments(rs.getInt("saleID"), rs.getInt("processNumber"),
                        rs.getDouble("totalCost"), rs.getDouble("firstPayment"),
                        rs.getDouble("monthlyInstallment"), rs.getInt("numberOfMonths"));
                installmentPaymentsList.add(installmentPayments);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return installmentPaymentsList;
    }

    public void showInstallmentPayments(TableView<InstallmentPayments> tv_installmentPayments) {
        tv_installmentPayments.setItems(getInstallmentPaymentsList());
    }

    //@FXML
    public void handleInstallmentRowSelection(MouseEvent event, TableView<InstallmentPayments> tv_installmentPayments, TextField txt_saleID, TextField txt_processNumber, TextField txt_totalCost, TextField txt_firstPayment, TextField txt_monthlyInstallment, TextField txt_numberOfMonths) {
        InstallmentPayments selectedPayment = tv_installmentPayments.getSelectionModel().getSelectedItem();
        if (selectedPayment != null) {
            txt_saleID.setText(String.valueOf(selectedPayment.getSaleID()));
            txt_processNumber.setText(String.valueOf(selectedPayment.getProcessNumber()));
            txt_totalCost.setText(String.valueOf(selectedPayment.getTotalCost()));
            txt_firstPayment.setText(String.valueOf(selectedPayment.getFirstPayment()));
            txt_monthlyInstallment.setText(String.valueOf(selectedPayment.getMonthlyInstallment()));
            txt_numberOfMonths.setText(String.valueOf(selectedPayment.getNumberOfMonths()));
        }
    }

    //@FXML
    public void insertInstallmentRecord(TextField txt_saleID, TextField txt_processNumber, TextField txt_totalCost, TextField txt_firstPayment, TextField txt_monthlyInstallment, TextField txt_numberOfMonths, TableView<InstallmentPayments> tv_installmentPayments) {
        try {
            int saleID = Integer.parseInt(txt_saleID.getText().trim());
            int processNumber = Integer.parseInt(txt_processNumber.getText().trim());
            double totalCost = Double.parseDouble(txt_totalCost.getText().trim());
            double firstPayment = Double.parseDouble(txt_firstPayment.getText().trim());
            double monthlyInstallment = Double.parseDouble(txt_monthlyInstallment.getText().trim());
            int numberOfMonths = Integer.parseInt(txt_numberOfMonths.getText().trim());

            if (!saleIDExists(saleID)) {
                displayAlert("Sale ID does not exist in Sales table.");
                return;
            }
            if (installmentIDExists(saleID, processNumber)) {
                displayAlert("Sale ID and Process Number combination already exists.");
                return;
            }

            String query = "INSERT INTO InstallmentPayments (saleID, processNumber, totalCost, firstPayment, monthlyInstallment, numberOfMonths) VALUES (?, ?, ?, ?, ?, ?)";
            executeUpdate(query, saleID, processNumber, totalCost, firstPayment, monthlyInstallment, numberOfMonths);
            displayAlert("Installment record inserted successfully.");
            showInstallmentPayments(tv_installmentPayments);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format. Please check the fields.");
        }
    }

    //@FXML
    public void updateInstallmentRecord(TextField txt_saleID, TextField txt_processNumber, TextField txt_totalCost, TextField txt_firstPayment, TextField txt_monthlyInstallment, TextField txt_numberOfMonths, TableView<InstallmentPayments> tv_installmentPayments) {
        try {
            int saleID = Integer.parseInt(txt_saleID.getText().trim());
            int processNumber = Integer.parseInt(txt_processNumber.getText().trim());
            double totalCost = Double.parseDouble(txt_totalCost.getText().trim());
            double firstPayment = Double.parseDouble(txt_firstPayment.getText().trim());
            double monthlyInstallment = Double.parseDouble(txt_monthlyInstallment.getText().trim());
            int numberOfMonths = Integer.parseInt(txt_numberOfMonths.getText().trim());

            if (!installmentIDExists(saleID, processNumber)) {
                displayAlert("Installment record does not exist.");
                return;
            }

            String query = "UPDATE InstallmentPayments SET totalCost = ?, firstPayment = ?, monthlyInstallment = ?, numberOfMonths = ? WHERE saleID = ? AND processNumber = ?";
            executeUpdate(query, totalCost, firstPayment, monthlyInstallment, numberOfMonths, saleID, processNumber);
            displayAlert("Installment record updated successfully.");
            showInstallmentPayments(tv_installmentPayments);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format. Please check the fields.");
        }
    }

    //@FXML
    public void deleteInstallmentRecord(TextField txt_saleID, TextField txt_processNumber, TableView<InstallmentPayments> tv_installmentPayments) {
        try {
            int saleID = Integer.parseInt(txt_saleID.getText().trim());
            int processNumber = Integer.parseInt(txt_processNumber.getText().trim());

            if (!installmentIDExists(saleID, processNumber)) {
                displayAlert("Installment record does not exist.");
                return;
            }

            String query = "DELETE FROM InstallmentPayments WHERE saleID = ? AND processNumber = ?";
            executeUpdate(query, saleID, processNumber);
            displayAlert("Installment record deleted successfully.");
            showInstallmentPayments(tv_installmentPayments);
        } catch (NumberFormatException e) {
            displayAlert("Invalid input format. Please check the fields.");
        }
    }

    private boolean saleIDExists(int saleID) {
        return recordExists("SELECT COUNT(*) FROM Sales WHERE saleID = ?", saleID);
    }

    private boolean installmentIDExists(int saleID, int processNumber) {
        return recordExists("SELECT COUNT(*) FROM InstallmentPayments WHERE saleID = ? AND processNumber = ?", saleID, processNumber);
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
