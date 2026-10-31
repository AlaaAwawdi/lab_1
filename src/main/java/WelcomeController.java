import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WelcomeController {

    @FXML
    private Label welcomeLabel;

    // Display the username after a successful login.
    public void setUsername(String username) {
        welcomeLabel.setText("Welcome, " + username + "!");
    }
}