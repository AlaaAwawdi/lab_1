public class UserState {
    private int failureCount = 0;
    private long blockedUntilMillis = 0L;

    public synchronized int recordFailure(int n, int t) {
        long now = System.currentTimeMillis();

        if (blockedUntilMillis > now) {
            return 0;
        }

        if (blockedUntilMillis != 0L) {
            failureCount = 0;
            blockedUntilMillis = 0L;
        }

        failureCount++;

        if (failureCount >= n) {
            blockedUntilMillis = now + t * 1000L;
            return 0;
        }

        return n - failureCount;
    }

    public synchronized long getRemainingBlockSeconds() {
        long remainingMillis = blockedUntilMillis - System.currentTimeMillis();

        if (remainingMillis <= 0) {
            if (blockedUntilMillis != 0L) {
                failureCount = 0;
                blockedUntilMillis = 0L;
            }
            return 0;
        }

        return (remainingMillis + 999L) / 1000L;
    }

    public synchronized boolean recordSuccess() {
        if (getRemainingBlockSeconds() > 0) {
            return false;
        }

        failureCount = 0;
        return true;
    }
}
