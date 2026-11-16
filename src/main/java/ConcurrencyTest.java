import java.util.concurrent.CountDownLatch;

public class ConcurrencyTest {

    public static String runTest() throws InterruptedException {
        AttemptTracker tracker = new AttemptTracker(10_000, 30);
        String email = "test@example.com";

        Thread[] threads = new Thread[20];
        CountDownLatch startSignal = new CountDownLatch(1);

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    startSignal.await();
                    for (int j = 0; j < 499; j++) {
                        tracker.recordFailure(email);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            threads[i].start();
        }

        startSignal.countDown();

        for (Thread thread : threads) {
            thread.join();
        }

        int remaining = -1;
        for (int i = 0; i < 19; i++) {
            remaining = tracker.recordFailure(email);
            if (remaining == 0) {
                return "FAIL: Account blocked before the 20th extra failure.";
            }
        }

        int countedBeforeExtras = 10_000 - remaining - 19;
        int afterTwentieth = tracker.recordFailure(email);

        return "Concurrent failures counted: " + countedBeforeExtras + " / 9980\n"
                + "Attempts left after 19 more failures: " + remaining + "\n"
                + "Blocked on the 20th extra failure: " + (afterTwentieth == 0);
    }
}
