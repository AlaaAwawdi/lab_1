import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private ArrayList<User> users;
    private Stage stage;

    // Receive the valid users from LoginApp.
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    // Receive the application window.
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Display file errors inside the screen and disable login.
    public void showLoadingError(String message) {
        errorLabel.setText(message);
        loginButton.setDisable(true);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        errorLabel.setText("");

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter a username and a password");
            return;
        }

        // User.toString() returns the username, a space, and the password.
        String enteredDetails = username + " " + password;

        for (User user : users) {
            if (user.toString().equals(enteredDetails)) {
                openWelcomeScreen(user.getUsername());
                return;
            }
        }

        // Show the error in the current screen, without a pop-up.
        errorLabel.setText("Incorrect username or password");
    }

    // Load the welcome FXML after a successful login.
    private void openWelcomeScreen(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    LoginController.class.getResource("/welcome.fxml")
            );

            Parent root = loader.load();

            WelcomeController controller = loader.getController();
            controller.setUsername(username);

            stage.setScene(new Scene(root));
            stage.setTitle("Welcome");
            stage.sizeToScene();

        } catch (IOException e) {
            errorLabel.setText("Could not open the welcome screen");
            e.printStackTrace();
        }
    }
}