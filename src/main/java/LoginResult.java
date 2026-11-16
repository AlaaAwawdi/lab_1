public record LoginResult(Status status, long value) {
    public enum Status {
        SUCCESS,
        INVALID_CREDENTIALS,
        WRONG_PASSWORD,
        BLOCKED
    }
}