package oose.euphoria.backend.presentation.dto.messages.responses;


import lombok.Getter;
import lombok.Setter;
import oose.euphoria.backend.data.entities.Message;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@SuppressWarnings("java:S1068")
public class ChatMessagesResponse {
    private Long senderID;
    private Long receiverID;
    private String type = "FULL_CHAT";
    private List<Message> messages;

    public ChatMessagesResponse() {
        this.messages = new ArrayList<>();
    }

    /**
     * Adds messages to the messages list.
     *
     * @param messages Messages to add to all chatmessages
     */
    public void addMessages(List<Message> messages) {
        this.messages.addAll(messages);
    }
}
