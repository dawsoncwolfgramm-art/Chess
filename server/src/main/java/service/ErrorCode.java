package service;

public enum ErrorCode {
    BAD_REQUEST(400, "bad request"),
    UNAUTHORIZED(401, "unauthorized"),
    ALREADY_TAKEN(403, "already taken");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int status() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
