import java.util.function.LongConsumer;
import javafx.application.Platform;

public class CountdownTask implements Runnable {
    private final String email;
    private final AttemptTracker tracker;
    private final LongConsumer onTick;

    public CountdownTask(String email, AttemptTracker tracker, LongConsumer onTick) {
        this.email = email;
        this.tracker = tracker;
        this.onTick = onTick;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                long seconds = tracker.getRemainingBlockSeconds(email);
                Platform.runLater(() -> onTick.accept(seconds));

                if (seconds == 0) {
                    return;
                }

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
