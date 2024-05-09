package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MainController implements Initializable{
	
	@FXML
    private Button btn_delete;

    @FXML
    private Button btn_insert;

    @FXML
    private Button btn_update;

    @FXML
    private TableColumn<Car, Integer> tv_carID;
    
    @FXML
    private TableColumn<Car, String> tv_carModel;

    @FXML
    private TableColumn<Car, String> tv_carType;
    
    @FXML
    private TableColumn<Car, String> tv_carColor;
    
    @FXML
    private TableColumn<Car, String> tv_carStatus;

    @FXML
    private TableView<Car> tv_cars;

    @FXML
    private TextField txt_carID;
    
    @FXML
    private TextField txt_carModel;

    @FXML
    private TextField txt_carType;
    
    @FXML
    private TextField txt_carColor;
    
    @FXML
    private TextField txt_carStatus;
    
   
   @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
      showcars();
    }
   
   public void handleRowSelection(MouseEvent event) {
	   System.out.println("INSIDE handleRowSelection.");
	    // Get selected row index
	    int index = tv_cars.getSelectionModel().getSelectedIndex();
	    System.out.println("Index is :" + index);
	    if (index <= -1) {
	        System.out.println("No row selected.");
	        return;
	    }

	    // Get selected row data from the ObservableList
	    Car selectedcar = tv_cars.getSelectionModel().getSelectedItem();
	    if (selectedcar != null) {
	        // Populate text fields with selected row's data
	    	txt_carID.setText(String.valueOf(selectedcar.getCarID()));
	        txt_carModel.setText(selectedcar.getCarModel());
	        txt_carType.setText(String.valueOf(selectedcar.getCarType()));
	        txt_carColor.setText(String.valueOf(selectedcar.getCarColor()));
	        txt_carStatus.setText(String.valueOf(selectedcar.getCarStatus()));
	    }
	}

   public ObservableList<Car> getcars(String query){
	    ObservableList<Car> carsList = FXCollections.observableArrayList();
	    Connection conn = DBConnection.getConnection();

	    Statement st = null; // Initialize st to null
	    ResultSet rs = null; // Initialize rs to null

	    try {
	        st = conn.createStatement();
	        rs = st.executeQuery(query);
	        Car cars;

	        if (!rs.isBeforeFirst()) {
	            System.out.println("No records found for the query: " + query);
	            return null; // Return null if no records found
	        } else {
	            while (rs.next()) {
	                cars = new Car(rs.getInt("carID"), rs.getString("carType"), rs.getString("carModel"), rs.getString("carColor"), rs.getString("carStatus"));
	                carsList.add(cars);
	            }
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    } finally {
	        // Close resources in finally block
	        try {
	            if (rs != null) rs.close();
	            if (st != null) st.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    return carsList;
	}
	
    public void showcars() {

		String query ="SELECT * FROM MG_PALESTINE_BRANCH.cars";
    	ObservableList<Car> carsList = getcars(query);
        // Bind columns with properties
        tv_carID.setCellValueFactory(new PropertyValueFactory<>("carID"));
        tv_carType.setCellValueFactory(new PropertyValueFactory<>("carType"));
        tv_carModel.setCellValueFactory(new PropertyValueFactory<>("carModel"));
        tv_carColor.setCellValueFactory(new PropertyValueFactory<>("carColor"));
        tv_carStatus.setCellValueFactory(new PropertyValueFactory<>("carStatus"));

        tv_cars.setItems(carsList);
    }
 
   
    public void insertRecord() {
        // Get carID from txt_carID
    	int carID = Integer.parseInt(txt_carID.getText().trim());

        System.out.println("carID: " +carID);
        // Check if carID exists
      if (carIDExists(carID)) {
            // Handle carID exists case (e.g., show error message)
            System.out.println("carID already exists");
            return;
        }
        
        // If carID doesn't exist, proceed with insertion
        String query = "INSERT INTO MG_PALESTINE_BRANCH.cars VALUES ('" + carID + "'," 
        		+ "'" + txt_carType.getText() + "'" + "," + "'" + txt_carModel.getText() + "'" 
        		+ "," + "'" + txt_carColor.getText()+ "'" 
        		+ "," + "'" + txt_carStatus.getText()+"'" + ")"; 

        executeQuery(query);
        showcars();
    }

    private boolean carIDExists(int carID){
        boolean flag = false;
        String query = "SELECT COUNT(*) FROM MG_PALESTINE_BRANCH.cars WHERE cars.carID = '" + carID +"'";
     
            ObservableList<Car> carsList = getcars(query);
            System.out.println("carsList.isEmpty(:)" + !carsList.isEmpty());
            if (!carsList.isEmpty()) {
                // carsList is not empty, meaning carID exists
                System.out.println("carID already exists.");
                displayAlert("carID already exists.");
                flag = true;
            }
         
        return flag;
    }
    private void displayAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void updateRecord(){
        String query = "UPDATE  MG_PALESTINE_BRANCH.cars SET CarType = " + "'" + txt_carType.getText()+ "'" + "," 
        		+" carModel = " +"'"+ txt_carModel.getText()+ "'" + "," 
        		+ "carColor = " +"'"+ txt_carColor.getText()+"'" + "," 
        		+ " CarStatus = " + "'" + txt_carStatus.getText() +"'" 
        		+ " WHERE carID = "  + Integer.parseInt(txt_carID.getText().trim());
        executeQuery(query);
        showcars();
    }
    


    public void deleteButton(){
        String query = "DELETE FROM MG_PALESTINE_BRANCH.cars WHERE carID =" + txt_carID.getText() + "";
        executeQuery(query);
        showcars();
    }

    public void executeQuery(String query) {
        Connection conn = DBConnection.getConnection();
        Statement st;
        try{
            st = conn.createStatement();
            st.executeUpdate(query);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


}
