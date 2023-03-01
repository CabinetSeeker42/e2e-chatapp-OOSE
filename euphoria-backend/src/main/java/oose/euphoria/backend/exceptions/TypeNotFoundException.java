package oose.euphoria.backend.exceptions;

public class TypeNotFoundException extends RuntimeException {
    public TypeNotFoundException(String type) {
        super(type);
    }
}
