package oose.euphoria.backend.exceptions;

public class DatabaseConnectionException extends RuntimeException {
    public DatabaseConnectionException(RuntimeException e) {
        super("Error while creating database session! " + e.getMessage());
    }
}
