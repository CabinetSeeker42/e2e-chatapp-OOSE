package oose.euphoria.backend.exceptions;

public class NotLoggedInException extends RuntimeException {
    public NotLoggedInException() {
        super("Not logged in!");
    }
}
