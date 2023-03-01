package oose.euphoria.backend.data;

import oose.euphoria.backend.configuration.database.IDatabaseConnection;
import oose.euphoria.backend.data.entities.Attachment;
import oose.euphoria.backend.data.entities.Message;
import oose.euphoria.backend.exceptions.AttachmentNotFoundException;
import oose.euphoria.backend.exceptions.DatabaseConnectionException;
import oose.euphoria.backend.utilities.TimeUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static oose.euphoria.backend.configuration.database.Queries.*;
import static oose.euphoria.backend.configuration.database.QueryParameters.*;

@Component
public class MessageManager implements IMessageManager {
    @Autowired
    IDatabaseConnection connection;

    /**
     * Inserts a message in the database, the database will return a message with an ID and readDate.
     *
     * @param content      Encrypted message
     * @param senderID     Sender UserID
     * @param receiverID   Receiver UserID
     * @param timestamp    String of timestamp
     * @param attachmentID ID of the saved attachment
     * @return Message with timestamp and ID
     */
    @Override
    public Message createMessage(String content,
                                 String senderID,
                                 String receiverID,
                                 String timestamp,
                                 String attachmentID,
                                 boolean isBroadcast) {
        Message message = new Message();
        message.setSendDate(timestamp);
        message.setContent(content);
        message.setReceiverID(receiverID);
        message.setSenderID(senderID);
        message.setAttachmentID(attachmentID);
        message.setBroadcast(isBroadcast);

        try (Session session = connection.openSession()) {
            session.beginTransaction();
            session.save(message);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
        return message;
    }

    /**
     * Inserts an attachment in the database, the database will return an attachment with an ID.
     *
     * @param attachmentType    Type of the attachment
     * @param attachmentName    The original name of the attachment
     * @param attachmentContent The encrypted attachment
     * @return Attachment with ID
     */
    @Override
    public Attachment createAttachment(String attachmentType,
                                       String attachmentName,
                                       String attachmentContent) {
        Attachment attachment = new Attachment();
        attachment.setType(attachmentType);
        attachment.setName(attachmentName);
        attachment.setContent(attachmentContent);

        try (Session session = connection.openSession()) {
            session.beginTransaction();
            session.save(attachment);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
        return attachment;
    }

    /**
     * Returns list of messages between the sender and the receiver.
     *
     * @param senderID   ID of the sender of the messages
     * @param receiverID ID of the receiver of the messages
     * @return List of messages
     */
    @Override
    public List<Message> getChatHistory(String senderID,
                                        String receiverID) {
        try (Session session = connection.openSession()) {
            Query<Message> query = session.createQuery(SELECT_MESSAGES_BETWEEN_USERS);
            query.setParameter(SENDER_ID_PARAMETER, senderID);
            query.setParameter(RECEIVER_ID_PARAMETER, receiverID);
            query.setParameter(ONE_DAY, TimeUtils.oneDay());
            return query.list();
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Returns all data from a specific attachment.
     *
     * @param attachmentID ID of the attachment
     * @return The entire attachment
     */
    @Override
    public Attachment getAttachment(String attachmentID) {
        try (Session session = connection.openSession()) {
            Query<Attachment> query = session.createQuery(SELECT_ATTACHMENT);
            query.setParameter(ATTACHMENT_ID_PARAMETER, attachmentID);
            return query
                    .getResultStream()
                    .findAny()
                    .orElseThrow(() -> new AttachmentNotFoundException(attachmentID));
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Deletes all messages based on time.
     */
    @Override
    public void deleteTimedMessages() {
        try (Session session = connection.openSession()) {
            Query<Message> query = session.createQuery(DELETE_MESSAGE_BY_TIME);
            query.setParameter(ONE_DAY, TimeUtils.oneDay());
            query.setParameter(ONE_WEEK, TimeUtils.oneWeek());
            Transaction transaction = session.beginTransaction();
            query.list().forEach(session::delete);
            transaction.commit();
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @Override
    public String getAttachmentName(String attachmentID) {
        try (Session session = connection.openSession()) {
            Query<String> query = session.createQuery(SELECT_ATTACHMENT_NAME);
            query.setParameter(ATTACHMENT_ID_PARAMETER, attachmentID);

            return query
                    .getResultStream()
                    .findAny()
                    .orElseThrow(() -> new AttachmentNotFoundException(attachmentID));
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Get all contacts with receiverID where the receiver has unread messages
     *
     * @param receiverID The contactID
     * @return List of userIDs where the receiver has unread messages
     */
    @Override
    public List<String> getContactsWithNotification(String receiverID) {
        try (Session session = connection.openSession()) {
            Query<String> query = session.createQuery(SELECT_UNREAD_MESSAGES_USERS);
            query.setParameter(RECEIVER_ID_PARAMETER, receiverID);
            return query.list();
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Get the amount of unread messages between sender and receiver
     *
     * @param senderID   The sender of the messages
     * @param receiverID The receiver of the messages
     * @return The amount of unread messages
     */
    @Override
    public Long getUnreadMessages(String senderID, String receiverID) {
        try (Session session = connection.openSession()) {
            Query<Long> query = session.createQuery(COUNT_UNREAD_MESSAGES);
            query.setParameter(SENDER_ID_PARAMETER, senderID);
            query.setParameter(RECEIVER_ID_PARAMETER, receiverID);
            return query.uniqueResult();
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @Override
    public void readChat(String receiverID, String senderID, String timestamp) {
        try (Session session = connection.openSession()) {
            Query<Message> query = session.createQuery(SELECT_UNREAD_MESSAGES);
            query.setParameter(SENDER_ID_PARAMETER, senderID);
            query.setParameter(RECEIVER_ID_PARAMETER, receiverID);
            Transaction transaction = session.beginTransaction();
            query.list().forEach(message -> {
                message.setReadDate(timestamp);
                session.update(message);
            });
            transaction.commit();
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @Override
    public boolean checkMessageFromUser(String senderID, int messageID) {
        try (Session session = connection.openSession()) {
            Query<Message> query = session.createQuery(SELECT_MESSAGE_BY_ID);
            query.setParameter(MESSAGE_ID_PARAMETER, messageID);
            Message message = query.getSingleResult();
            return message.getSenderID().equals(senderID);
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @Override
    public void deleteMessage(int messageID) {
        try (Session session = connection.openSession()) {
            Transaction transaction = session.beginTransaction();
            Query<Message> query = session.createQuery(SELECT_MESSAGE_BY_ID);
            query.setParameter(MESSAGE_ID_PARAMETER, messageID);
            Message messageToDelete = query.getSingleResult();
            session.delete(messageToDelete);
            transaction.commit();
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @Override
    public List<Message> getBroadcasts(String receiverID) {
        try (Session session = connection.openSession()) {
            Query<Message> query = session.createQuery(SELECT_BROADCASTS);
            query.setParameter(RECEIVER_ID_PARAMETER, receiverID);
            return query.list();
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
