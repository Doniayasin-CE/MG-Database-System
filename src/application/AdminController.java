package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    // Customer Fields
    @FXML
    private TextField customerIDField, customerNameField, emailField, ninField, phoneField;

    // Customer Table
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> customerIDColumn;
    @FXML
    private TableColumn<Customer, String> customerNameColumn, emailColumn, ninColumn;

    // Phone Table
    @FXML
    private TableView<CustomersPhone> phoneTable;
    @FXML
    private TableColumn<CustomersPhone, Integer> phoneCustomerIDColumn;
    @FXML
    private TableColumn<CustomersPhone, Long> phoneColumn;

    // Car Fields
    @FXML
    private TextField carIDField, carModelField, carTypeField, carColorField, carStatusField;

    // Car Table
    @FXML
    private TableView<Car> carTable;
    @FXML
    private TableColumn<Car, Integer> carIDColumn;
    @FXML
    private TableColumn<Car, String> carModelColumn, carTypeColumn, carColorColumn, carStatusColumn;

    // Service Fields
    @FXML
    private TextField serviceIDField, serviceCustomerIDField, carIDField1, serviceDescriptionField;

    // Service Table
    @FXML
    private TableView<Service> serviceTable;
    @FXML
    private TableColumn<Service, Integer> serviceIDColumn, serviceCustomerIDColumn, serviceCarIDColumn;
    @FXML
    private TableColumn<Service, String> serviceDescriptionColumn;

    // Employee Fields
    @FXML
    private TextField employeeIDField, employeeNameField, salaryField;
    @FXML
    private DatePicker dateOfEmploymentPicker;

    // Employee Table
    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, Integer> employeeIDColumn;
    @FXML
    private TableColumn<Employee, String> employeeNameColumn;
    @FXML
    private TableColumn<Employee, Double> salaryColumn;
    @FXML
    private TableColumn<Employee, LocalDate> dateOfEmploymentColumn;

    // Advertiser Fields
    @FXML
    private TextField advertiserEmployeeIDField, pointsField;

    // Advertiser Table
    @FXML
    private TableView<Advertisers> advertiserTable;
    @FXML
    private TableColumn<Advertisers, Integer> advertiserEmployeeIDColumn, pointsColumn;

    // Manager Fields
    @FXML
    private TextField managerEmployeeIDField, monthlyReportField;

    // Manager Table
    @FXML
    private TableView<Managers> managerTable;
    @FXML
    private TableColumn<Managers, Integer> managerEmployeeIDColumn;
    @FXML
    private TableColumn<Managers, String> monthlyReportColumn;

    // Salesman Fields
    @FXML
    private TextField salesmanEmployeeIDField, employeeTargetField;

    // Salesman Table
    @FXML
    private TableView<Salesmans> salesmanTable;
    @FXML
    private TableColumn<Salesmans, Integer> salesmanEmployeeIDColumn, employeeTargetColumn;

    // Sale Fields
    @FXML
    private TextField saleIDField, saleDescriptionField, saleCustomerIDField, saleCarIDField, saleEmployeeIDField;

    // Sale Table
    @FXML
    private TableView<Sale> salesTable;
    @FXML
    private TableColumn<Sale, Integer> saleIDColumn;
    @FXML
    private TableColumn<Sale, String> saleDescriptionColumn;
    @FXML
    private TableColumn<Sale, Integer> saleCustomerIDColumn, saleCarIDColumn, saleEmployeeIDColumn;

    // Cash Fields
    @FXML
    private TextField cashSaleIDField, cashProcessNumberField, cashBankCashField, cashDirectCashField, cashDiscountsField;

    // Cash Table
    @FXML
    private TableView<Cashs> cashTable;
    @FXML
    private TableColumn<Cashs, Integer> cashSaleIDColumn, cashProcessNumberColumn;
    @FXML
    private TableColumn<Cashs, Double> cashBankCashColumn, cashDirectCashColumn;
    @FXML
    private TableColumn<Cashs, Float> cashDiscountsColumn;

    // InstallmentPayments Fields
    @FXML
    private TableColumn<InstallmentPayments, Integer> tv_saleID;
    @FXML
    private TableColumn<InstallmentPayments, Integer> tv_processNumber;
    @FXML
    private TableColumn<InstallmentPayments, Double> tv_totalCost;
    @FXML
    private TableColumn<InstallmentPayments, Double> tv_firstPayment;
    @FXML
    private TableColumn<InstallmentPayments, Double> tv_monthlyInstallment;
    @FXML
    private TableColumn<InstallmentPayments, Integer> tv_numberOfMonths;
    @FXML
    private TableView<InstallmentPayments> tv_installmentPayments;
    @FXML
    private TextField txt_saleID;
    @FXML
    private TextField txt_processNumber;
    @FXML
    private TextField txt_totalCost;
    @FXML
    private TextField txt_firstPayment;
    @FXML
    private TextField txt_monthlyInstallment;
    @FXML
    private TextField txt_numberOfMonths;

    //@FXML
    //private TabPane tabPane;

    // Controller Instances
    private CustomerController customerController;
    private CarController carController;
    private ServiceController serviceController;
    private EmployeeController employeeController;
    private AdvertiserController advertiserController;
    private ManagersController managersController;
    private SalesmansController salesmansController;
    private SalesController salesController;
    private CashsController cashsController;
    private InstallmentPaymentsController installmentPaymentsController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize controller instances
        customerController = new CustomerController();
        carController = new CarController();
        serviceController = new ServiceController();
        employeeController = new EmployeeController();
        advertiserController = new AdvertiserController();
        managersController = new ManagersController();
        salesmansController = new SalesmansController();
        salesController = new SalesController();
        cashsController = new CashsController();
        installmentPaymentsController = new InstallmentPaymentsController();

        // Call the initialization methods of each controller
        customerController.initializeCustomerTable(customerTable, customerIDColumn, customerNameColumn, emailColumn, ninColumn);
        customerController.initializePhoneTable(phoneTable, phoneCustomerIDColumn, phoneColumn);
        carController.initializeCarTable(carTable, carIDColumn, carTypeColumn, carModelColumn, carColorColumn, carStatusColumn);
        serviceController.initializeServiceTable(serviceTable, serviceIDColumn, serviceCustomerIDColumn, serviceCarIDColumn, serviceDescriptionColumn);
        employeeController.initializeEmployeeTable(employeeTable, employeeIDColumn, employeeNameColumn, salaryColumn, dateOfEmploymentColumn);
        advertiserController.initializeAdvertiserTable(advertiserTable, advertiserEmployeeIDColumn, pointsColumn);
        managersController.initializeManagerTable(managerTable, managerEmployeeIDColumn, monthlyReportColumn);
        salesmansController.initializeSalesmanTable(salesmanTable, salesmanEmployeeIDColumn, employeeTargetColumn);
        salesController.initializeSaleTable(salesTable, saleIDColumn, saleDescriptionColumn, saleCustomerIDColumn, saleCarIDColumn, saleEmployeeIDColumn);
        cashsController.initializeCashTable(cashTable, cashSaleIDColumn, cashProcessNumberColumn, cashBankCashColumn, cashDirectCashColumn, cashDiscountsColumn);
        installmentPaymentsController.initializeInstallmentPaymentsTable(tv_installmentPayments, tv_saleID, tv_processNumber, tv_totalCost, tv_firstPayment, tv_monthlyInstallment, tv_numberOfMonths);

        loadAllData();
    }

    private void loadAllData() {
        customerController.showCustomers(customerTable);
        customerController.showCustomerPhones(phoneTable);
        carController.showCars(carTable);
        serviceController.showServices(serviceTable);
        employeeController.showEmployees(employeeTable);
        advertiserController.showAdvertisers(advertiserTable);
        managersController.showManagers(managerTable);
        salesmansController.showSalesmans(salesmanTable);
        salesController.showSales(salesTable);
        cashsController.showCashs(cashTable);
        installmentPaymentsController.showInstallmentPayments(tv_installmentPayments);
    }

    // Delegating Customer actions to CustomerController
    @FXML
    public void handleCustomerRowSelection(MouseEvent event) {
        customerController.handleCustomerRowSelection(event, customerTable, customerIDField, customerNameField, emailField, ninField, phoneTable);
    }

    @FXML
    public void addCustomer() {
        customerController.addCustomer(customerIDField, customerNameField, emailField, ninField, customerTable);
    }

    @FXML
    public void updateCustomer() {
        customerController.updateCustomer(customerIDField, customerNameField, emailField, ninField, customerTable);
    }

    @FXML
    public void deleteCustomer() {
        customerController.deleteCustomer(customerIDField, customerTable);
    }

    @FXML
    public void handlePhoneRowSelection(MouseEvent event) {
        customerController.handlePhoneRowSelection(event, phoneTable, customerIDField, phoneField);
    }

    @FXML
    public void addPhone() {
        customerController.addPhone(customerIDField, phoneField, phoneTable);
    }

    @FXML
    public void updatePhone() {
        customerController.updatePhone(customerIDField, phoneField, phoneTable);
    }

    @FXML
    public void deletePhone() {
        customerController.deletePhone(customerIDField, phoneField, phoneTable);
    }

    // Delegating Car actions to CarController
    @FXML
    public void handleCarRowSelection(MouseEvent event) {
        carController.handleCarRowSelection(event, carTable, carIDField, carModelField, carTypeField, carColorField, carStatusField);
    }

    @FXML
    public void addCar() {
        carController.addCar(carIDField, carModelField, carTypeField, carColorField, carStatusField, carTable);
    }

    @FXML
    public void updateCar() {
        carController.updateCar(carIDField, carModelField, carTypeField, carColorField, carStatusField, carTable);
    }

    @FXML
    public void deleteCar() {
        carController.deleteCar(carIDField, carTable);
    }

    // Delegating Service actions to ServiceController
    @FXML
    public void handleServiceRowSelection(MouseEvent event) {
        serviceController.handleServiceRowSelection(event, serviceTable, serviceIDField, serviceCustomerIDField, carIDField1, serviceDescriptionField);
    }

    @FXML
    public void addService() {
        serviceController.addService(serviceIDField, serviceCustomerIDField, carIDField1, serviceDescriptionField, serviceTable);
    }

    @FXML
    public void updateService() {
        serviceController.updateService(serviceIDField, serviceCustomerIDField, carIDField1, serviceDescriptionField, serviceTable);
    }

    @FXML
    public void deleteService() {
        serviceController.deleteService(serviceIDField, serviceTable);
    }

    // Delegating Employee actions to EmployeeController
    @FXML
    public void handleEmployeeRowSelection() {
        employeeController.handleEmployeeRowSelection(employeeTable, employeeIDField, employeeNameField, salaryField, dateOfEmploymentPicker);
    }

    @FXML
    public void addEmployee() {
        employeeController.addEmployee(employeeIDField, employeeNameField, salaryField, dateOfEmploymentPicker, employeeTable);
    }

    @FXML
    public void updateEmployee() {
        employeeController.updateEmployee(employeeIDField, employeeNameField, salaryField, dateOfEmploymentPicker, employeeTable);
    }

    @FXML
    public void deleteEmployee() {
        employeeController.deleteEmployee(employeeIDField, employeeTable);
    }

    // Delegating Advertiser actions to advertiserController
    @FXML
    public void handleAdvertiserRowSelection(MouseEvent event) {
        advertiserController.handleAdvertiserRowSelection(event,advertiserTable, advertiserEmployeeIDField, pointsField);
    }

    @FXML
    public void addAdvertiser() {
        advertiserController.addAdvertiser(advertiserTable, advertiserEmployeeIDField, pointsField);
    }

    @FXML
    public void updateAdvertiser() {
        advertiserController.updateAdvertiser(advertiserTable, advertiserEmployeeIDField, pointsField);
    }

    @FXML
    public void deleteAdvertiser() {
        advertiserController.deleteAdvertiser(advertiserTable, advertiserEmployeeIDField);
    }

    // Delegating Manager actions to ManagersController
    @FXML
    public void handleManagerRowSelection(MouseEvent event) {
        managersController.handleManagerRowSelection(event,managerTable, managerEmployeeIDField, monthlyReportField);
    }

    @FXML
    public void addManager() {
        managersController.addManager(managerEmployeeIDField, monthlyReportField, managerTable);
    }

    @FXML
    public void updateManager() {
        managersController.updateManager(managerEmployeeIDField, monthlyReportField, managerTable);
    }

    @FXML
    public void deleteManager() {
        managersController.deleteManager(managerEmployeeIDField, managerTable);
    }

    // Delegating Salesman actions to SalesmansController
    @FXML
    public void handleSalesmanRowSelection(MouseEvent event) {
        salesmansController.handleSalesmanRowSelection(event,salesmanTable, salesmanEmployeeIDField, employeeTargetField);
    }

    @FXML
    public void addSalesman() {
        salesmansController.addSalesman(salesmanEmployeeIDField, employeeTargetField, salesmanTable);
    }

    @FXML
    public void updateSalesman() {
        salesmansController.updateSalesman(salesmanEmployeeIDField, employeeTargetField, salesmanTable);
    }

    @FXML
    public void deleteSalesman() {
        salesmansController.deleteSalesman(salesmanEmployeeIDField, salesmanTable);
    }

    // Delegating Sale actions to SalesController
    @FXML
    public void handleSaleRowSelection(MouseEvent event) {
        salesController.handleSaleRowSelection(event, salesTable, saleIDField, saleDescriptionField, saleCustomerIDField, saleCarIDField, saleEmployeeIDField);
    }

    @FXML
    public void addSale() {
        salesController.addSale(saleIDField, saleDescriptionField, saleCustomerIDField, saleCarIDField, saleEmployeeIDField, salesTable);
    }

    @FXML
    public void updateSale() {
        salesController.updateSale(saleIDField, saleDescriptionField, saleCustomerIDField, saleCarIDField, saleEmployeeIDField, salesTable);
    }

    @FXML
    public void deleteSale() {
        salesController.deleteSale(saleIDField, salesTable);
    }

    // Delegating Cash actions to CashsController
    @FXML
    public void handleCashRowSelection(MouseEvent event) {
        cashsController.handleCashRowSelection(event, cashTable, cashSaleIDField, cashProcessNumberField, cashBankCashField, cashDirectCashField, cashDiscountsField);
    }

    @FXML
    public void addCash() {
        cashsController.addCash(cashSaleIDField, cashProcessNumberField, cashBankCashField, cashDirectCashField, cashDiscountsField, cashTable);
    }

    @FXML
    public void updateCash() {
        cashsController.updateCash(cashSaleIDField, cashProcessNumberField, cashBankCashField, cashDirectCashField, cashDiscountsField, cashTable);
    }

    @FXML
    public void deleteCash() {
        cashsController.deleteCash(cashSaleIDField, cashProcessNumberField, cashTable);
    }

    // Delegating Installment Payments actions to InstallmentPaymentsController
    @FXML
    public void handleInstallmentRowSelection(MouseEvent event) {
        installmentPaymentsController.handleInstallmentRowSelection(event, tv_installmentPayments, txt_saleID, txt_processNumber, txt_totalCost, txt_firstPayment, txt_monthlyInstallment, txt_numberOfMonths);
    }

    @FXML
    public void insertInstallmentRecord() {
        installmentPaymentsController.insertInstallmentRecord(txt_saleID, txt_processNumber, txt_totalCost, txt_firstPayment, txt_monthlyInstallment, txt_numberOfMonths, tv_installmentPayments);
    }

    @FXML
    public void updateInstallmentRecord() {
        installmentPaymentsController.updateInstallmentRecord(txt_saleID, txt_processNumber, txt_totalCost, txt_firstPayment, txt_monthlyInstallment, txt_numberOfMonths, tv_installmentPayments);
    }

    @FXML
    public void deleteInstallmentRecord() {
        installmentPaymentsController.deleteInstallmentRecord(txt_saleID, txt_processNumber, tv_installmentPayments);
    }
}
