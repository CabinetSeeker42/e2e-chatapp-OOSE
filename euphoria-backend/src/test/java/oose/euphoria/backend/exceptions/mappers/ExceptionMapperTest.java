package oose.euphoria.backend.exceptions.mappers;

import oose.euphoria.backend.exceptions.*;
import oose.euphoria.backend.exceptions.mappers.errorresponse.WebsocketError;
import oose.euphoria.backend.presentation.dto.messages.responses.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionMapperTest {
    public static final String MESSAGE_MOCK = "a message";

    @Spy
    ExceptionMapper sut;

    @Test
    void parseDatabaseConnectionExceptionTest() {
        // Arrange
        DatabaseConnectionException exception = assertThrows(
                DatabaseConnectionException.class,
                () -> {
                    throw new DatabaseConnectionException(new RuntimeException(MESSAGE_MOCK));
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals("Error while creating database session! " + MESSAGE_MOCK, exception.getMessage());
        assertEquals(errorMessage.getType(), WebsocketError.DATABASE_ERROR.getType());
        assertEquals("Error in the database!", errorMessage.getError());
    }

    @Test
    void parseDatabaseSessionExceptionTest() {
        // Arrange
        DatabaseSessionException exception = assertThrows(
                DatabaseSessionException.class,
                () -> {
                    throw new DatabaseSessionException(new RuntimeException(MESSAGE_MOCK));
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals("Error while creating database session! " + MESSAGE_MOCK, exception.getMessage());
        assertEquals("Error in the session!", errorMessage.getError());
    }

    @Test
    void parseMessageContentMissingExceptionTest() {
        // Arrange
        MessageContentMissingException exception = assertThrows(
                MessageContentMissingException.class,
                () -> {
                    throw new MessageContentMissingException(MESSAGE_MOCK);
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals(MESSAGE_MOCK, exception.getMessage());
        assertEquals("Missing something in the message content!", errorMessage.getError());
    }

    @Test
    void parseUserNotFoundExceptionTest() {
        // Arrange
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> {
                    throw new UserNotFoundException(MESSAGE_MOCK);
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals("User not found! " + MESSAGE_MOCK, exception.getMessage());
        assertEquals("User not found!", errorMessage.getError());
    }

    @Test
    void parseUserAlreadyExistsExceptionTest() {
        // Arrange
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> {
                    throw new UserAlreadyExistsException(new RuntimeException(MESSAGE_MOCK));
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals("That user already exists! " + MESSAGE_MOCK, exception.getMessage());
        assertEquals("User already exists!", errorMessage.getError());
    }

    @Test
    void parseFunctionNotImplementedExceptionTest() {
        // Arrange
        FunctionNotImplementedException exception = assertThrows(
                FunctionNotImplementedException.class,
                () -> {
                    throw new FunctionNotImplementedException(MESSAGE_MOCK);
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals("Function not implemented yet! " + MESSAGE_MOCK, exception.getMessage());
        assertEquals("Feature not implemented yet!", errorMessage.getError());
    }

    @Test
    void parseUNKNOWN_ERRORTest() {
        // Arrange
        Exception exception = assertThrows(
                Exception.class,
                () -> {
                    throw new Exception(MESSAGE_MOCK);
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals(MESSAGE_MOCK, exception.getMessage());
        assertEquals("Unknown error occurred, please report!", errorMessage.getError());
    }

    @Test
    void parseNotLoggedInExceptionTest() {
        // Arrange
        NotLoggedInException exception = assertThrows(
                NotLoggedInException.class,
                () -> {
                    throw new NotLoggedInException();
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals("Session not logged in!", errorMessage.getError());
    }

    @Test
    void parseLoginRejectedExceptionTest() {
        // Arrange
        LoginRejectedException exception = assertThrows(
                LoginRejectedException.class,
                () -> {
                    throw new LoginRejectedException();
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals("Challenge failed!", errorMessage.getError());
    }

    @Test
    void parseCryptoExceptionTest() {
        // Arrange
        CryptoException exception = assertThrows(
                CryptoException.class,
                () -> {
                    throw new CryptoException(MESSAGE_MOCK);
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals("Something went wrong while encrypting!", errorMessage.getError());
    }

    @Test
    void parseUserNotOwnerOfMessageExceptionTest() {
        // Arrange
        UserNotOwnerOfMessageException exception = assertThrows(
                UserNotOwnerOfMessageException.class,
                () -> {
                    throw new UserNotOwnerOfMessageException(MESSAGE_MOCK);
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals("This user is not the sender of this message!", errorMessage.getError());
    }

    @Test
    void parseAttachmentNotFoundExceptionTest() {
        // Arrange
        AttachmentNotFoundException exception = assertThrows(
                AttachmentNotFoundException.class,
                () -> {
                    throw new AttachmentNotFoundException(MESSAGE_MOCK);
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals("Attachment not found! " + MESSAGE_MOCK, exception.getMessage());
        assertEquals("Attachment not found!", errorMessage.getError());
    }

    @Test
    void parseTypeNotFoundExceptionExceptionTest() {
        // Arrange
        TypeNotFoundException exception = assertThrows(
                TypeNotFoundException.class,
                () -> {
                    throw new TypeNotFoundException(MESSAGE_MOCK);
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals(MESSAGE_MOCK, exception.getMessage());
        assertEquals("Message type not found!", errorMessage.getError());
    }

    @Test
    void parseJWTTokenDecoderExceptionExceptionTest() {
        // Arrange
        JWTTokenDecoderException exception = assertThrows(
                JWTTokenDecoderException.class,
                () -> {
                    throw new JWTTokenDecoderException(new Exception(MESSAGE_MOCK));
                }
        );

        // Act
        ErrorResponse errorMessage = sut.parse(exception);

        // Assert
        assertEquals(MESSAGE_MOCK, exception.getMessage());
        assertEquals("Username malformed!", errorMessage.getError());
    }
}