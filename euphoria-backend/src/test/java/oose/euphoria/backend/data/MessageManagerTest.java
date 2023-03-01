package oose.euphoria.backend.data;

import oose.euphoria.backend.configuration.database.IDatabaseConnection;
import oose.euphoria.backend.data.entities.Attachment;
import oose.euphoria.backend.data.entities.Message;
import oose.euphoria.backend.exceptions.AttachmentNotFoundException;
import oose.euphoria.backend.exceptions.DatabaseConnectionException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageManagerTest {
    // Mocks for message tests
    private final int MESSAGE_ID_MOCK = 1;
    private final String USER_1_MOCK = "user1";
    private final String USER_2_MOCK = "user2";
    private final String MESSAGE_MOCK = "123";
    private final String READ_DATE_MOCK = "01-01-2020 00:00:00";
    private final Message messageWithContent = new Message(USER_1_MOCK);
    private final List<Message> messages = new ArrayList<>(
            List.of(
                    new Message(USER_1_MOCK),
                    new Message(USER_2_MOCK)
            )
    );
    private final Message messageWithoutContent = new Message();

    // Mocks for attachment tests
    private final String ATTACHMENT_ID_MOCK = "1";
    private final String ATTACHMENT_TYPE_MOCK = "type";
    private final String ATTACHMENT_NAME_MOCK = "name";
    private final String ATTACHMENT_CONTENT_MOCK = "content";
    private final Attachment ATTACHMENT_MOCK = new Attachment(ATTACHMENT_ID_MOCK, ATTACHMENT_TYPE_MOCK, ATTACHMENT_NAME_MOCK, ATTACHMENT_CONTENT_MOCK);

    @Spy
    @InjectMocks
    MessageManager sut;
    @Mock
    private IDatabaseConnection connection;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Query query;

    @BeforeEach
    void setUp() {
        // This line is used to add a mock of @autowired classes into the sut.
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMessageTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.save(any())).thenReturn(null);
        when(session.getTransaction()).thenReturn(transaction);

        // Act
        Message actual = sut.createMessage(MESSAGE_MOCK, USER_1_MOCK, USER_2_MOCK, READ_DATE_MOCK, null, false);

        // Assert
        assertEquals(MESSAGE_MOCK, actual.getContent());
    }

    @Test
    void createMessageWithAttachmentTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.save(any())).thenReturn(null);
        when(session.getTransaction()).thenReturn(transaction);

        // Act
        Message actual = sut.createMessage(MESSAGE_MOCK, USER_1_MOCK, USER_2_MOCK, READ_DATE_MOCK, ATTACHMENT_ID_MOCK, false);

        // Assert
        assertEquals(ATTACHMENT_ID_MOCK, actual.getAttachmentID());
    }

    @Test
    void createMessageThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.save(any())).thenThrow(HibernateException.class);
        when(session.getTransaction()).thenReturn(transaction);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.createMessage(MESSAGE_MOCK, USER_1_MOCK, USER_2_MOCK, READ_DATE_MOCK, null, false));
    }

    @Test
    void createAttachmentTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.save(any())).thenReturn(null);
        when(session.getTransaction()).thenReturn(transaction);

        // Act
        Attachment actual = sut.createAttachment(ATTACHMENT_TYPE_MOCK, ATTACHMENT_NAME_MOCK, ATTACHMENT_CONTENT_MOCK);

        // Assert
        assertEquals(ATTACHMENT_TYPE_MOCK, actual.getType());
        assertEquals(ATTACHMENT_NAME_MOCK, actual.getName());
        assertEquals(ATTACHMENT_CONTENT_MOCK, actual.getContent());
    }

    @Test
    void createAttachmentThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.save(any())).thenThrow(HibernateException.class);
        when(session.getTransaction()).thenReturn(transaction);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.createAttachment(ATTACHMENT_TYPE_MOCK, ATTACHMENT_NAME_MOCK, ATTACHMENT_CONTENT_MOCK));
    }

    @Test
    void getChatHistoryTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.list()).thenReturn(null);

        // Act
        List<Message> actual = sut.getChatHistory(USER_1_MOCK, USER_2_MOCK);

        // Assert
        assertNull(actual);
    }

    @Test
    void getChatHistoryThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.list()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.getChatHistory(USER_1_MOCK, USER_2_MOCK));
    }

    @Test
    void getAttachmentTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(ATTACHMENT_MOCK));

        // Act
        Attachment actual = sut.getAttachment(ATTACHMENT_ID_MOCK);

        // Assert
        assertNotNull(actual);
        assertEquals(ATTACHMENT_MOCK.getId(), actual.getId());
    }

    @Test
    void getAttachmentThrowAttachmentExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenThrow(AttachmentNotFoundException.class);

        // Assert & Act
        assertThrows(AttachmentNotFoundException.class, () -> sut.getAttachment(ATTACHMENT_MOCK.getId()));
    }

    @Test
    void getAttachmentThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.getAttachment(ATTACHMENT_MOCK.getId()));
    }

    @Test
    void deleteTimedMessageTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.getTransaction()).thenReturn(transaction);
        when(session.createQuery(anyString())).thenReturn(query);

        // Assert & Act
        assertDoesNotThrow(() -> sut.deleteTimedMessages());
    }

    @Test
    void deleteTimedMessageThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenThrow(HibernateException.class);
        when(session.createQuery(anyString())).thenReturn(query);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.deleteTimedMessages());
    }

    @Test
    void getAttachmentNameTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(ATTACHMENT_NAME_MOCK));

        // Act
        String actual = sut.getAttachmentName(ATTACHMENT_ID_MOCK);

        // Assert
        assertNotNull(actual);
        assertEquals(ATTACHMENT_MOCK.getName(), actual);
    }

    @Test
    void getAttachmentNameThrowAttachmentExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenThrow(AttachmentNotFoundException.class);

        // Assert & Act
        assertThrows(AttachmentNotFoundException.class, () -> sut.getAttachmentName(ATTACHMENT_ID_MOCK));
    }

    @Test
    void getAttachmentNameThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.getAttachmentName(ATTACHMENT_ID_MOCK));
    }

    @Test
    void getContactsWithNotificationTest() {
        // Arrange
        List<String> contacts = new ArrayList<>();
        String notification = "notification";
        contacts.add(notification);

        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.list()).thenReturn(contacts);

        // Act
        List<String> actual = sut.getContactsWithNotification(USER_1_MOCK);

        // Assert
        assertEquals(actual.get(0), notification);
    }

    @Test
    void getContactsWithoutNotificationTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.list()).thenReturn(null);

        // Act
        List<String> actual = sut.getContactsWithNotification(USER_1_MOCK);

        // Assert
        assertNull(actual);
    }

    @Test
    void getContactsWithNotificationThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.list()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.getContactsWithNotification(USER_1_MOCK));
    }


    @Test
    void getUnreadMessagesTest() {
        // Arrange
        Long message = 123L;

        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(message);

        // Act
        Long actual = sut.getUnreadMessages(USER_1_MOCK, USER_2_MOCK);

        // Assert
        assertEquals(actual, message);
    }

    @Test
    void getNoUnreadMessagesTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(null);

        // Act
        Long actual = sut.getUnreadMessages(USER_1_MOCK, USER_2_MOCK);

        // Assert
        assertNull(actual);
    }

    @Test
    void getUnreadMessagesThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.uniqueResult()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.getUnreadMessages(USER_1_MOCK, USER_2_MOCK));
    }

    @Test
    void readChatTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(session.beginTransaction()).thenReturn(transaction);
        doReturn(messages).when(query).list();
        doNothing().when(transaction).commit();

        // Act
        sut.readChat(USER_1_MOCK, USER_2_MOCK, READ_DATE_MOCK);

        // Assert
        verify(session, times(1)).createQuery(anyString());
        verify(session, times(2)).update(any());
    }

    @Test
    void readChatThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.list()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.readChat(USER_1_MOCK, USER_2_MOCK, READ_DATE_MOCK));
    }

    @Test
    void checkMessageFromUserTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(messageWithContent);

        // Act
        boolean res = sut.checkMessageFromUser(USER_1_MOCK, MESSAGE_ID_MOCK);

        // Assert
        assertEquals(USER_1_MOCK, messageWithContent.getSenderID());
        assertTrue(res);
    }

    @Test
    void checkMessageFromUserNotUserTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(messageWithContent);

        // Act
        boolean res = sut.checkMessageFromUser(USER_2_MOCK, MESSAGE_ID_MOCK);

        // Assert
        assertNotEquals(USER_1_MOCK, messageWithoutContent.getSenderID());
        assertFalse(res);
    }

    @Test
    void checkMessageFromUserThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.checkMessageFromUser(USER_1_MOCK, MESSAGE_ID_MOCK));
    }

    @Test
    void deleteMessageTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.getTransaction()).thenReturn(transaction);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(messageWithoutContent);
        doNothing().when(session).delete(any(Message.class));
        doNothing().when(transaction).commit();

        // Act
        sut.deleteMessage(anyInt());

        // Assert
        verify(transaction).commit();
    }

    @Test
    void deleteMessageThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.deleteMessage(MESSAGE_ID_MOCK));
    }

    @Test
    void getBroadcastsTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.list()).thenReturn(messages);

        // Act
        List<Message> actual = sut.getBroadcasts(USER_1_MOCK);

        // Assert
        assertEquals(messages, actual);
    }

    @Test
    void getBroadcastsThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.getBroadcasts(USER_1_MOCK));
    }
}
