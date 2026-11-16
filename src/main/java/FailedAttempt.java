import java.util.concurrent.ArrayBlockingQueue;

public class FailedAttempt
{

    private final String email;
    private final ArrayBlockingQueue<Integer> replyQueue = new ArrayBlockingQueue<>(1);

    public FailedAttempt(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public ArrayBlockingQueue<Integer> getReplyQueue() {
        return replyQueue;
    }

}
