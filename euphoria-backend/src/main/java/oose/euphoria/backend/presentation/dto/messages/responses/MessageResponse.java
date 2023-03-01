package oose.euphoria.backend.presentation.dto.messages.responses;

import lombok.Getter;
import lombok.Setter;
import oose.euphoria.backend.data.entities.Message;

@Setter
@Getter
@SuppressWarnings("java:S1068")
public class MessageResponse {
    private String type = "INCOMING_MESSAGE";
    private Message message;

    /**
     * Returns a new message response.
     *
     * @param message
     */
    public MessageResponse(Message message) {
        this.message = message;
    }
}
