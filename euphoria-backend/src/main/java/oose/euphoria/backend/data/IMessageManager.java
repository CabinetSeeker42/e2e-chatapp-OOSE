package oose.euphoria.backend.data;

import oose.euphoria.backend.data.entities.Attachment;
import oose.euphoria.backend.data.entities.Message;

import java.util.List;

public interface IMessageManager {
    Message createMessage(String content,
                          String senderID,
                          String receiverID,
                          String timestamp,
                          String attachmentID,
                          boolean isBroadcast);

    Attachment createAttachment(String attachmentType,
                                String attachmentName,
                                String attachmentContent);

    List<Message> getChatHistory(String senderID, String receiverID);

    Attachment getAttachment(String attachmentID);

    void deleteTimedMessages();

    String getAttachmentName(String attachmentID);

    List<String> getContactsWithNotification(String receiverID);

    Long getUnreadMessages(String senderID, String receiverID);

    void readChat(String senderID, String receiverID, String timestamp);

    boolean checkMessageFromUser(String senderID, int messageID);

    void deleteMessage(int messageID);

    List<Message> getBroadcasts(String receiverID);
}
