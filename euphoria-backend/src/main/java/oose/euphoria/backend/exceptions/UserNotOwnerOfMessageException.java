package oose.euphoria.backend.exceptions;

public class UserNotOwnerOfMessageException extends RuntimeException {
    public UserNotOwnerOfMessageException(String userID) {
        super("User not owner of message! " + userID);
    }
}
