
public class BlockCheckThread extends Thread {
    private final AttemptTracker tracker;
    private final String email;
    private long remainingSeconds;

    public BlockCheckThread(AttemptTracker tracker, String email) {
        this.tracker = tracker;
        this.email = email;
    }

    @Override
    public void run() {
        remainingSeconds = tracker.getRemainingBlockSeconds(email);
    }

    public long getRemainingSeconds() {
        return remainingSeconds;
    }
}
