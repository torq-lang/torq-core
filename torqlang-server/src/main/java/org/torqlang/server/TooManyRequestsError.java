package org.torqlang.server;

public class TooManyRequestsError extends RuntimeException {

    public TooManyRequestsError() {
    }

    public TooManyRequestsError(String message) {
        super(message);
    }

    public TooManyRequestsError(String message, Throwable cause) {
        super(message, cause);
    }

    public TooManyRequestsError(Throwable cause) {
        super(cause);
    }

}
