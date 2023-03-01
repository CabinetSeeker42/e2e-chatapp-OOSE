package oose.euphoria.backend.exceptions;

public class LoginRejectedException extends RuntimeException {
    public LoginRejectedException() {
        super("Login rejected!");
    }
}
