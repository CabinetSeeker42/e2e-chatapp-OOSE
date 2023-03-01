package oose.euphoria.backend.exceptions;

public class JWTTokenDecoderException extends RuntimeException {
    public JWTTokenDecoderException(Exception e) {
        super(e.getMessage());
    }
}
