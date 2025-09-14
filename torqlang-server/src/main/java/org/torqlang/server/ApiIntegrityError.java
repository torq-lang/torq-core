package org.torqlang.server;

public class ApiIntegrityError extends RuntimeException {

    public ApiIntegrityError() {
    }

    public ApiIntegrityError(String message) {
        super(message);
    }

    public ApiIntegrityError(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiIntegrityError(Throwable cause) {
        super(cause);
    }

}
