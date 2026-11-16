import java.util.concurrent.BlockingQueue;

public class FailureWorker extends Thread {
    private final BlockingQueue<FailedAttempt> attempts;
    private final AttemptTracker tracker;

    public FailureWorker(BlockingQueue<FailedAttempt> attempts,
                         AttemptTracker tracker) {
        this.attempts = attempts;
        this.tracker = tracker;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                FailedAttempt attempt = attempts.take();
                int remaining = tracker.recordFailure(attempt.getEmail());
                attempt.getReplyQueue().put(remaining);
            }
        } catch (InterruptedException e) {
            interrupt();
        }
    }
}