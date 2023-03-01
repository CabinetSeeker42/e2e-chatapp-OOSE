package oose.euphoria.backend.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(RuntimeException e) {
        super("That user already exists! " + e.getMessage());
    }
}
