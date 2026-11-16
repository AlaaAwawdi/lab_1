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
import java.util.concurrent.BlockingQueue;

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
    private AttemptTracker tracker;
    private BlockingQueue<FailedAttempt> attempts;

    public void setAttemptTracking(AttemptTracker tracker,
                                   BlockingQueue<FailedAttempt> attempts) {
        this.tracker = tracker;
        this.attempts = attempts;
    }

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
        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter a username and a password");
            return;
        }

        errorLabel.setText("");
        setLoginDisabled(true);

        LoginTask task = new LoginTask(email, password, users, tracker, attempts,
                result -> {
                    setLoginDisabled(false);

                    switch (result.status()) {
                        case SUCCESS ->
                                openWelcomeScreen(email);
                        case INVALID_CREDENTIALS ->
                                errorLabel.setText("Incorrect username or password");
                        case WRONG_PASSWORD ->
                                errorLabel.setText("Incorrect username or password. Attempts left: "
                                        + result.value());
                        case BLOCKED -> startCountdown(email);
                    }
                });

        Thread loginThread = new Thread(task);
        loginThread.setDaemon(true);
        loginThread.start();
    }

    private void setLoginDisabled(boolean disabled) {
        usernameField.setDisable(disabled);
        passwordField.setDisable(disabled);
        loginButton.setDisable(disabled);
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
    private void startCountdown(String email) {
        setLoginDisabled(true);

        CountdownTask countdown = new CountdownTask(email, tracker, seconds -> {
            if (seconds == 0) {
                errorLabel.setText("");
                setLoginDisabled(false);
            } else {
                errorLabel.setText("Account blocked. Seconds remaining: " + seconds);
            }
        });

        Thread countdownThread = new Thread(countdown);
        countdownThread.setDaemon(true);
        countdownThread.start();
    }
}