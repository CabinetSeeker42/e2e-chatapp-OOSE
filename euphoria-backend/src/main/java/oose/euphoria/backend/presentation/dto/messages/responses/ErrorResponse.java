package oose.euphoria.backend.presentation.dto.messages.responses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@SuppressWarnings("java:S1068")
public class ErrorResponse {
    private String error;
    private String type = "ERROR_MESSAGE";

    /**
     * Returns a new error message.
     *
     * @param message Error message
     */
    public ErrorResponse(String message) {
        this.error = message;
    }
}
