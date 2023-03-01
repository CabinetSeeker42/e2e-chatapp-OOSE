package oose.euphoria.backend.presentation.dto.messages;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@SuppressWarnings("java:S1068")
public class MessageRequest {
    private int messageID;
    private String senderID;
    private String receiverID;
    private String content;
    private String publicKey;
    private String companyID;
    private String jwtToken;
    private String challengeSolution;
    private String attachmentType;
    private String attachmentName;
    private String attachmentContent;
    private String attachmentID;
    private String type;
    private boolean isBroadcast;
    private boolean isSupport;
}
