import java.util.concurrent.ConcurrentHashMap;

public class AttemptTracker {
    private final int n;
    private final int t;
    private final ConcurrentHashMap<String, UserState> states =
            new ConcurrentHashMap<>();

    public AttemptTracker(int n, int t) {
        this.n = n;
        this.t = t;
    }

    private UserState stateFor(String email) {
        return states.computeIfAbsent(email, key -> new UserState());
    }

    public int recordFailure(String email) {
        return stateFor(email).recordFailure(n, t);
    }

    public long getRemainingBlockSeconds(String email) {
        UserState state = states.get(email);
        return state == null ? 0L : state.getRemainingBlockSeconds();
    }

    public boolean recordSuccess(String email) {
        return stateFor(email).recordSuccess();
    }
}
