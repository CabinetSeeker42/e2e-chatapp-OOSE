package oose.euphoria.backend.service;

import oose.euphoria.backend.data.entities.Attachment;
import oose.euphoria.backend.data.entities.User;
import oose.euphoria.backend.presentation.dto.messages.responses.*;


public interface IMessageService {
    ChatMessagesResponse getChatHistory(String senderID, String receiverID);

    Attachment saveAttachment(String attachmentType, String attachmentName, String attachmentContent);

    AttachmentResponse getAttachmentById(String attachmentID);

    void deleteTimedMessages();

    MessageResponse saveMessage(String message,
                                String senderID,
                                String receiverID,
                                String attachmentType,
                                String attachmentName,
                                String attachmentContent,
                                boolean isAnnouncement);

    NotificationResponse getNotifications(User user);

    void readChat(String senderID, String receiverID);

    ChatMessagesResponse deleteMessage(String receiverID, String senderID, int messageID);

    boolean checkIfUserIsSender(String senderID, int messageID);

    BroadcastResponse getBroadcasts(String receiverID);
}
