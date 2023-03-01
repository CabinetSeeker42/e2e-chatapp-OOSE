package oose.euphoria.backend.exceptions.mappers.errorresponse;

import oose.euphoria.backend.presentation.dto.messages.responses.ErrorResponse;

public class WebsocketError {


    public static final ErrorResponse DATABASE_ERROR = new ErrorResponse("Error in the database!");
    public static final ErrorResponse SESSION_ERROR = new ErrorResponse("Error in the session!");
    public static final ErrorResponse CONTENT_ERROR = new ErrorResponse("Missing something in the message content!");
    public static final ErrorResponse USER_NOT_FOUND = new ErrorResponse("User not found!");
    public static final ErrorResponse USER_ALREADY_EXISTS = new ErrorResponse("User already exists!");
    public static final ErrorResponse FEATURE_NOT_IMPLEMENTED = new ErrorResponse("Feature not implemented yet!");
    public static final ErrorResponse NOT_LOGGED_IN = new ErrorResponse("Session not logged in!");
    public static final ErrorResponse LOGIN_REJECTED = new ErrorResponse("Challenge failed!");
    public static final ErrorResponse UNKNOWN_ERROR = new ErrorResponse("Unknown error occurred, please report!");
    public static final ErrorResponse CRYPTO_EXCEPTION = new ErrorResponse("Something went wrong while encrypting!");
    public static final ErrorResponse USER_NOT_SENDER = new ErrorResponse("This user is not the sender of this message!");
    public static final ErrorResponse TYPE_NOT_FOUND = new ErrorResponse("Message type not found!");
    public static final ErrorResponse USERNAME_MALFORMED = new ErrorResponse("Username malformed!");
    public static final ErrorResponse ATTACHMENT_NOT_FOUND = new ErrorResponse("Attachment not found!");
    private WebsocketError() {
    }
}
