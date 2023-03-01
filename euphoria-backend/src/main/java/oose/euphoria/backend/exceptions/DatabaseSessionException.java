package oose.euphoria.backend.exceptions;

public class DatabaseSessionException extends RuntimeException {
    public DatabaseSessionException(Exception e) {
        super("Error while creating database session! " + e.getMessage());
    }
}
