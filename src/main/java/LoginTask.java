import java.util.function.Consumer;
import javafx.application.Platform;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class LoginTask implements Runnable {
    private final String email;
    private final AttemptTracker tracker;
    private final Consumer<LoginResult> onResult;
    private final String password;
    private final List<User> users;
    private final BlockingQueue<FailedAttempt> attempts;

    public LoginTask(String email, String password, List<User> users,
                     AttemptTracker tracker, BlockingQueue<FailedAttempt> attempts,
                     Consumer<LoginResult> onResult) {
        this.email = email;
        this.password = password;
        this.users = users;
        this.tracker = tracker;
        this.attempts = attempts;
        this.onResult = onResult;
    }
    @Override
    public void run() {
        BlockCheckThread check = new BlockCheckThread(tracker, email);
        check.start();

        try {
            check.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        long remainingSeconds = check.getRemainingSeconds();

        if (remainingSeconds > 0) {
            LoginResult result = new LoginResult(
                    LoginResult.Status.BLOCKED, remainingSeconds
            );
            Platform.runLater(() -> onResult.accept(result));
            return;
        }

        User account = null;
        for (User user : users) {
            if (user.getUsername().equals(email)) {
                account = user;
                break;
            }
        }

        if (account == null) {
            LoginResult result = new LoginResult(
                    LoginResult.Status.INVALID_CREDENTIALS, 0
            );
            Platform.runLater(() -> onResult.accept(result));
            return;
        }

        boolean matches = false;
        try {
            User entered = new User(email, password);
            matches = account.toString().equals(entered.toString());
        } catch (Exception e) {
            // The entered credentials failed the validation from Lab 1.
        }

        LoginResult result;

        if (matches && tracker.recordSuccess(email)) {
            result = new LoginResult(LoginResult.Status.SUCCESS, 0);
        } else if (matches) {
            // Another attempt blocked the account while we checked the password.
            result = new LoginResult(
                    LoginResult.Status.BLOCKED,
                    tracker.getRemainingBlockSeconds(email)
            );
        } else {
            FailedAttempt attempt = new FailedAttempt(email);
            try {
                attempts.put(attempt);
                int remaining = attempt.getReplyQueue().take();

                if (remaining > 0) {
                    result = new LoginResult(LoginResult.Status.WRONG_PASSWORD, remaining);
                } else {
                    result = new LoginResult(
                            LoginResult.Status.BLOCKED,
                            tracker.getRemainingBlockSeconds(email)
                    );
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        Platform.runLater(() -> onResult.accept(result));
    }
}