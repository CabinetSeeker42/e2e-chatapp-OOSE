package oose.euphoria.backend.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userID) {
        super("User not found! " + userID);
    }
}
