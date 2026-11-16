import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LoginApp extends Application {

    private final ArrayList<User> users = new ArrayList<>();
    private final BlockingQueue<FailedAttempt> attempts = new LinkedBlockingQueue<>();
    private AttemptTracker tracker;
    // Read the input file and store only valid users.
    private void loadUsers() throws FileNotFoundException {
        try (Scanner reader = new Scanner(new File("users.txt"))) {

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] parts = line.trim().split("\\s+");

                // Each line must contain a username and a password.
                if (parts.length != 2) {
                    System.out.println(
                            line + " -> Expected a username and a password"
                    );
                    continue;
                }

                try {
                    // User's constructor performs the validation.
                    User user = new User(parts[0], parts[1]);
                    users.add(user);

                } catch (Exception e) {
                    System.out.println(line + " -> " + e.getMessage());
                }
            }
        }
    }

    private String loadingError;

    @Override
    public void init() {
        try {
            loadUsers();
        } catch (FileNotFoundException e) {
            loadingError = "Could not open users.txt";
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        int n = Integer.parseInt(getParameters().getRaw().get(0));
        int t = Integer.parseInt(getParameters().getRaw().get(1));
        tracker = new AttemptTracker(n, t);

        for (int i = 0; i < 2; i++) {
            FailureWorker worker = new FailureWorker(attempts, tracker);
            worker.start();
        }

        FXMLLoader loader = new FXMLLoader(
                LoginApp.class.getResource("/login.fxml")
        );

        Parent root = loader.load();

        LoginController controller = loader.getController();
        controller.setUsers(users);
        controller.setAttemptTracking(tracker, attempts);
        controller.setStage(stage);

        if (loadingError != null) {
            controller.showLoadingError(loadingError);
        }

        stage.setTitle("Users Login");
        stage.setScene(new Scene(root));

        // Closing the window ends the application.
        stage.setOnCloseRequest(event -> Platform.exit());

        stage.show();
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        if (args.length > 2) {
            System.out.println("Expected exactly two arguments: n and t. Please re-enter them.");
            args = new String[0];
        }

        String n = readPositiveInt(args, 0, "n", input);
        if (n == null) {
            return;
        }

        String t = readPositiveInt(args, 1, "t", input);
        if (t == null) {
            return;
        }

        launch(n, t);
    }

    private static String readPositiveInt(String[] args, int index,
                                          String name, Scanner input) {
        String value = index < args.length ? args[index] : null;

        while (true) {
            if (value == null) {
                System.out.println("Missing argument: " + name);
            } else {
                try {
                    if (Integer.parseInt(value) >= 1) {
                        return value;
                    }
                } catch (NumberFormatException e) {
                    // The value could not be read as an integer.
                }
                System.out.println(name + " must be a positive integer.");
            }

            System.out.print("Enter " + name + ": ");
            System.out.flush();
            if (!input.hasNextLine()) {
                return null;
            }
            value = input.nextLine().trim();
        }
    }
}