package oose.euphoria.backend.service;

import oose.euphoria.backend.data.IMessageManager;
import oose.euphoria.backend.data.IUserManager;
import oose.euphoria.backend.data.entities.Attachment;
import oose.euphoria.backend.data.entities.User;
import oose.euphoria.backend.exceptions.UserNotOwnerOfMessageException;
import oose.euphoria.backend.presentation.dto.Broadcast;
import oose.euphoria.backend.presentation.dto.Notification;
import oose.euphoria.backend.presentation.dto.messages.responses.*;
import oose.euphoria.backend.presentation.dto.messages.staticmessage.GeneratorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageService implements IMessageService {
    @Autowired
    IMessageManager messageManager;

    @Autowired
    IUserManager userManager;

    @Autowired
    GeneratorMessage generatorMessage;

    /**
     * Handles the logic for getting all messages between two users.
     *
     * @param senderID   ID of the sender
     * @param receiverID ID of the receiver
     * @return messages between sender and receiver
     */
    @Override
    public ChatMessagesResponse getChatHistory(String senderID,
                                               String receiverID) {
        ChatMessagesResponse chatMessages = new ChatMessagesResponse();
        chatMessages.addMessages(messageManager.getChatHistory(senderID, receiverID));
        chatMessages.getMessages().forEach(
                message -> {
                    if (message.getAttachmentID() != null) {
                        message.setAttachmentName(messageManager.getAttachmentName(message.getAttachmentID()));
                    }
                }
        );
        return chatMessages;
    }

    /**
     * Handles the logic for saving a message.
     *
     * @param message           Encrypted message content
     * @param senderID          Sender UserID
     * @param receiverID        Receiver UserID
     * @param attachmentType    Type of the attachment
     * @param attachmentName    Original name of the attachment
     * @param attachmentContent The encrypted attachment
     * @return Incoming message but with the ID and datetime
     */
    @Override
    public MessageResponse saveMessage(String message,
                                       String senderID,
                                       String receiverID,
                                       String attachmentType,
                                       String attachmentName,
                                       String attachmentContent,
                                       boolean isAnnouncement) {
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();
        String attachmentID = null;

        if (attachmentContent != null) {
            Attachment attachment = saveAttachment(attachmentType, attachmentName, attachmentContent);
            attachmentID = attachment.getId();
        }
        MessageResponse messageResponse = new MessageResponse(messageManager.createMessage(message, senderID, receiverID, timestamp, attachmentID, isAnnouncement));
        messageResponse.getMessage().setAttachmentName(attachmentName);
        return messageResponse;
    }

    /**
     * Handles the logic for saving an attachment.
     *
     * @param attachmentType    Type of the attachment
     * @param attachmentName    Original name of the attachment
     * @param attachmentContent The encrypted attachment
     * @return The attachment but with the created ID
     */
    @Override
    public Attachment saveAttachment(String attachmentType, String attachmentName, String attachmentContent) {
        return messageManager.createAttachment(attachmentType, attachmentName, attachmentContent);
    }

    /**
     * Gets all data from a specific attachment.
     *
     * @param attachmentID ID of the attachment
     * @return The entire attachment
     */
    @Override
    public AttachmentResponse getAttachmentById(String attachmentID) {
        return new AttachmentResponse(messageManager.getAttachment(attachmentID));
    }

    /**
     * Deletes all messages based on time.
     */
    @Override
    public void deleteTimedMessages() {
        messageManager.deleteTimedMessages();
    }

    @Override
    public NotificationResponse getNotifications(User user) {
        List<Notification> notifications = new ArrayList<>();
        messageManager.getContactsWithNotification(user.getId())
                .forEach(
                        contact -> {
                            Long unreadCount = messageManager.getUnreadMessages(contact, user.getId());
                            if (unreadCount > 0) {
                                notifications.add(new Notification(contact, unreadCount));
                            }
                        }
                );
        return new NotificationResponse(notifications);
    }

    @Override
    public void readChat(String senderID, String receiverID) {
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();
        messageManager.readChat(senderID, receiverID, timestamp);
    }

    /**
     * Deletes a specific message from the database.
     *
     * @param receiverID The receiver of the send message
     * @param senderID   The sender of the send message
     * @param messageID  The message ID
     * @return
     */
    @Override
    public ChatMessagesResponse deleteMessage(String receiverID, String senderID, int messageID) {
        if (!checkIfUserIsSender(senderID, messageID)) {
            throw new UserNotOwnerOfMessageException(senderID);
        }
        messageManager.deleteMessage(messageID);
        return getChatHistory(senderID, receiverID);
    }

    /**
     * A check to see if the user is the owner of the message.
     *
     * @param senderID  The Sender of the message
     * @param messageID The message that has to be checked
     * @return Returns if the user is the owner of the message
     */
    @Override
    public boolean checkIfUserIsSender(String senderID, int messageID) {
        return messageManager.checkMessageFromUser(senderID, messageID);
    }


    /**
     * Returns a BroadcastResponse that contains a broadcast for each message.
     *
     * @param senderID ID of the sender who should obtain the broadcast
     * @return BroadcastResponse object with all broadcasts in it
     */
    @Override
    public BroadcastResponse getBroadcasts(String senderID) {
        List<Broadcast> broadcasts = new ArrayList<>();
        messageManager.getBroadcasts(senderID)
                .forEach(message -> broadcasts.add(new Broadcast(message, userManager.getUser(message.getSenderID()).getPublicKey())));
        return new BroadcastResponse(broadcasts);
    }
}
