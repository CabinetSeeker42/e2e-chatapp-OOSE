package oose.euphoria.backend.exceptions;

public class MessageContentMissingException extends RuntimeException {
    public MessageContentMissingException(String content) {
        super(content);
    }
}
