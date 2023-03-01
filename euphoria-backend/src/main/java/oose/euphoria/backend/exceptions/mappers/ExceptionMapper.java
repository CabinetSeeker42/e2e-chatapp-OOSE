package oose.euphoria.backend.exceptions.mappers;

import oose.euphoria.backend.exceptions.*;
import oose.euphoria.backend.exceptions.mappers.errorresponse.WebsocketError;
import oose.euphoria.backend.presentation.dto.messages.responses.ErrorResponse;

public class ExceptionMapper {
    private ExceptionMapper() {
    }

    /**
     * Returns a specific WebsocketErrorMessage based on the exception instance type.
     *
     * @param e Exception that should be parsed
     * @return The ErrorMessage object representing the incoming Exception
     */
    public static ErrorResponse parse(Exception e) {//NOSONAR
        if (e instanceof DatabaseConnectionException) return WebsocketError.DATABASE_ERROR;
        if (e instanceof DatabaseSessionException) return WebsocketError.SESSION_ERROR;
        if (e instanceof MessageContentMissingException) return WebsocketError.CONTENT_ERROR;
        if (e instanceof UserNotFoundException) return WebsocketError.USER_NOT_FOUND;
        if (e instanceof UserAlreadyExistsException) return WebsocketError.USER_ALREADY_EXISTS;
        if (e instanceof NotLoggedInException) return WebsocketError.NOT_LOGGED_IN;
        if (e instanceof LoginRejectedException) return WebsocketError.LOGIN_REJECTED;
        if (e instanceof FunctionNotImplementedException) return WebsocketError.FEATURE_NOT_IMPLEMENTED;
        if (e instanceof CryptoException) return WebsocketError.CRYPTO_EXCEPTION;
        if (e instanceof UserNotOwnerOfMessageException) return WebsocketError.USER_NOT_SENDER;
        if (e instanceof TypeNotFoundException) return WebsocketError.TYPE_NOT_FOUND;
        if (e instanceof JWTTokenDecoderException) return WebsocketError.USERNAME_MALFORMED;
        if (e instanceof AttachmentNotFoundException) return WebsocketError.ATTACHMENT_NOT_FOUND;
        return WebsocketError.UNKNOWN_ERROR;
    }
}
