package oose.euphoria.backend.exceptions;

public class FunctionNotImplementedException extends RuntimeException {
    public FunctionNotImplementedException(String e) {
        super("Function not implemented yet! " + e);
    }
}
