package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class mainController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            FXMLLoader loader = new FXMLLoader();
            Scene scene;
            if (username.equals("admin") && password.equals("admin")) {
                loader.setLocation(getClass().getResource("AdminController.fxml"));
                scene = new Scene(loader.load());
            } else if (username.equals("user") && password.equals("user")) {
                loader.setLocation(getClass().getResource("UserController.fxml"));
                scene = new Scene(loader.load());
            } else {
                errorLabel.setText("Invalid username or password");
                return;
            }
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
