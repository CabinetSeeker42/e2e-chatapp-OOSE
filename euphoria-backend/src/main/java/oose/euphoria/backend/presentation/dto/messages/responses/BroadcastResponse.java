package oose.euphoria.backend.presentation.dto.messages.responses;

import lombok.Getter;
import lombok.Setter;
import oose.euphoria.backend.presentation.dto.Broadcast;

import java.util.List;

@Getter
@Setter
@SuppressWarnings("java:S1068")
public class BroadcastResponse {
    private String type = "BROADCASTS";
    private List<Broadcast> messages;

    public BroadcastResponse(List<Broadcast> messages) {
        this.messages = messages;
    }
}
