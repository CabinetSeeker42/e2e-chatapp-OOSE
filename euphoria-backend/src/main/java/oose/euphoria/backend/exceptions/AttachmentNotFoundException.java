package oose.euphoria.backend.exceptions;

public class AttachmentNotFoundException extends RuntimeException {
    public AttachmentNotFoundException(String attachmentID) {
        super("Attachment not found! " + attachmentID);
    }
}