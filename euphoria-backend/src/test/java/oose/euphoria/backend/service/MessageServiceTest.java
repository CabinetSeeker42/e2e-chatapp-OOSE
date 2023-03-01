package oose.euphoria.backend.service;

import oose.euphoria.backend.data.IMessageManager;
import oose.euphoria.backend.data.IUserManager;
import oose.euphoria.backend.data.entities.Attachment;
import oose.euphoria.backend.data.entities.Message;
import oose.euphoria.backend.data.entities.User;
import oose.euphoria.backend.exceptions.UserNotOwnerOfMessageException;
import oose.euphoria.backend.presentation.dto.Broadcast;
import oose.euphoria.backend.presentation.dto.Notification;
import oose.euphoria.backend.presentation.dto.messages.responses.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {
    private final String USER_1_MOCK = "user1";
    private final String USER_2_MOCK = "user2";
    private final String MESSAGE_MOCK = "123";
    private final Message MESSAGE_CLASS_MOCK = new Message();
    private final Message MESSAGE_CLASS_MOCK_ATTACHMENT = new Message(0, "1", null, null, null, null, "3", false, "STAy pLEASE");
    private final ArrayList<Message> MESSAGES_MOCK = new ArrayList<>(Arrays.asList(MESSAGE_CLASS_MOCK, MESSAGE_CLASS_MOCK, MESSAGE_CLASS_MOCK, MESSAGE_CLASS_MOCK));
    private final String NO_ATTACHMENT = null;
    private final String ATTACHMENT_ID_MOCK = "1";
    private final String ATTACHMENT_TYPE_MOCK = "type";
    private final String ATTACHMENT_NAME_MOCK = "name";
    private final String ATTACHMENT_CONTENT_MOCK = "content";

    @Spy
    @InjectMocks
    MessageService sut;
    @Mock
    private IMessageManager messageManager;
    @Mock
    private IUserManager userManager;

    @BeforeEach
    void setup() {
        // This line is used to add a mock of @autowired classes into the sut.
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getChatHistoryTest() {
        // Arrange
        List<Message> messagesList = new ArrayList<>(Arrays.asList(MESSAGE_CLASS_MOCK, MESSAGE_CLASS_MOCK));

        when(messageManager.getChatHistory(anyString(), anyString())).thenReturn(messagesList);

        // Act
        ChatMessagesResponse chatMessagesActual = sut.getChatHistory(USER_1_MOCK, USER_2_MOCK);

        // Assert
        assertEquals(chatMessagesActual.getMessages().get(0), messagesList.get(0));
        assertEquals(chatMessagesActual.getMessages().get(1), messagesList.get(1));
    }

    @Test
    void getChatHistoryWithAttachmentTest() {
        // Arrange
        List<Message> messagesList = new ArrayList<>(Arrays.asList(MESSAGE_CLASS_MOCK, MESSAGE_CLASS_MOCK_ATTACHMENT));

        when(messageManager.getChatHistory(anyString(), anyString())).thenReturn(messagesList);
        when(messageManager.getAttachmentName(anyString())).thenReturn(ATTACHMENT_NAME_MOCK);

        // Act
        ChatMessagesResponse chatMessagesActual = sut.getChatHistory(USER_1_MOCK, USER_2_MOCK);

        // Assert
        assertEquals(chatMessagesActual.getMessages().get(0).getAttachmentName(), messagesList.get(0).getAttachmentName());
        assertEquals(chatMessagesActual.getMessages().get(1).getAttachmentName(), messagesList.get(1).getAttachmentName());
    }

    @Test
    void getChatHistoryWrongSenderTest() {
        // Arrange
        List<Message> messagesList = Collections.singletonList(MESSAGE_CLASS_MOCK);

        when(messageManager.getChatHistory(anyString(), anyString())).thenReturn(messagesList);

        // Act
        ChatMessagesResponse chatMessagesActual = sut.getChatHistory(USER_2_MOCK, USER_1_MOCK);

        // Assert
        assertNotEquals(chatMessagesActual.getMessages().get(0).getSenderID(), USER_1_MOCK);
    }

    @Test
    void saveMessageTest() {
        // Arrange
        Message messageClassMock = new Message();
        messageClassMock.setContent(MESSAGE_MOCK);
        messageClassMock.setSenderID(USER_1_MOCK);
        messageClassMock.setReceiverID(USER_2_MOCK);

        when(messageManager.createMessage(anyString(), anyString(), anyString(), any(), any(), anyBoolean())).thenReturn(messageClassMock);

        // Act
        MessageResponse actual = sut.saveMessage(MESSAGE_MOCK, USER_1_MOCK, USER_2_MOCK, NO_ATTACHMENT, NO_ATTACHMENT, NO_ATTACHMENT, false);

        // Assert
        assertEquals(MESSAGE_MOCK, actual.getMessage().getContent());
        assertEquals(USER_1_MOCK, actual.getMessage().getSenderID());
        assertEquals(USER_2_MOCK, actual.getMessage().getReceiverID());
    }

    @Test
    void saveMessageWithAttachmentTest() {
        // Arrange
        Message messageClassMock = new Message();
        messageClassMock.setContent(MESSAGE_MOCK);
        messageClassMock.setSenderID(USER_1_MOCK);
        messageClassMock.setReceiverID(USER_2_MOCK);
        messageClassMock.setAttachmentID(ATTACHMENT_ID_MOCK);

        Attachment attachmentClassMock = new Attachment();
        attachmentClassMock.setId(ATTACHMENT_ID_MOCK);
        attachmentClassMock.setType(ATTACHMENT_TYPE_MOCK);
        attachmentClassMock.setName(ATTACHMENT_NAME_MOCK);
        attachmentClassMock.setContent(ATTACHMENT_CONTENT_MOCK);

        when(messageManager.createAttachment(anyString(), anyString(), anyString())).thenReturn(attachmentClassMock);
        when(messageManager.createMessage(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(messageClassMock);

        // Act
        MessageResponse actual = sut.saveMessage(MESSAGE_MOCK, USER_1_MOCK, USER_2_MOCK, ATTACHMENT_TYPE_MOCK, ATTACHMENT_NAME_MOCK, ATTACHMENT_CONTENT_MOCK, false);

        // Assert
        assertEquals(ATTACHMENT_ID_MOCK, actual.getMessage().getAttachmentID());
        assertEquals(ATTACHMENT_NAME_MOCK, actual.getMessage().getAttachmentName());
    }

    @Test
    void saveWrongMessageTest() {
        // Arrange
        Message messageClassMock = new Message();
        messageClassMock.setContent(MESSAGE_MOCK);
        messageClassMock.setSenderID(USER_1_MOCK);
        messageClassMock.setReceiverID(USER_2_MOCK);

        when(messageManager.createMessage(anyString(), anyString(), anyString(), any(), any(), anyBoolean())).thenReturn(messageClassMock);

        // Act
        MessageResponse actual = sut.saveMessage(MESSAGE_MOCK, USER_1_MOCK, USER_2_MOCK, null, null, null, false);

        // Assert
        assertNotEquals("1234", actual.getMessage().getContent());
        assertNotEquals(USER_2_MOCK, actual.getMessage().getSenderID());
        assertNotEquals(USER_1_MOCK, actual.getMessage().getReceiverID());
    }

    @Test
    void saveAttachmentTest() {
        // Arrange
        Attachment attachmentClassMock = new Attachment();
        attachmentClassMock.setType(ATTACHMENT_TYPE_MOCK);
        attachmentClassMock.setName(ATTACHMENT_NAME_MOCK);
        attachmentClassMock.setContent(ATTACHMENT_CONTENT_MOCK);

        when(messageManager.createAttachment(anyString(), anyString(), anyString())).thenReturn(attachmentClassMock);

        // Act
        Attachment actual = sut.saveAttachment(ATTACHMENT_TYPE_MOCK, ATTACHMENT_NAME_MOCK, ATTACHMENT_CONTENT_MOCK);

        // Assert
        assertEquals(ATTACHMENT_TYPE_MOCK, actual.getType());
        assertEquals(ATTACHMENT_NAME_MOCK, actual.getName());
        assertEquals(ATTACHMENT_CONTENT_MOCK, actual.getContent());
    }

    @Test
    void saveWrongAttachmentTest() {
        // Arrange
        Attachment attachmentClassMock = new Attachment();
        attachmentClassMock.setType(ATTACHMENT_TYPE_MOCK);
        attachmentClassMock.setName(ATTACHMENT_NAME_MOCK);
        attachmentClassMock.setContent(ATTACHMENT_CONTENT_MOCK);

        when(messageManager.createAttachment(anyString(), anyString(), anyString())).thenReturn(attachmentClassMock);

        // Act
        Attachment actual = sut.saveAttachment(ATTACHMENT_TYPE_MOCK, ATTACHMENT_NAME_MOCK, ATTACHMENT_CONTENT_MOCK);

        // Assert
        assertNotEquals("wrong type", actual.getType());
        assertNotEquals("wrong name", actual.getName());
        assertNotEquals("wrong content", actual.getContent());
    }

    @Test
    void getAttachmentByIdTest() {
        // Arrange
        Attachment attachmentClassMock = new Attachment();
        attachmentClassMock.setId(ATTACHMENT_ID_MOCK);
        attachmentClassMock.setType(ATTACHMENT_TYPE_MOCK);
        attachmentClassMock.setName(ATTACHMENT_NAME_MOCK);
        attachmentClassMock.setContent(ATTACHMENT_CONTENT_MOCK);

        when(messageManager.getAttachment(anyString())).thenReturn(attachmentClassMock);

        // Act
        AttachmentResponse actual = sut.getAttachmentById(ATTACHMENT_ID_MOCK);

        // Assert
        assertEquals(ATTACHMENT_ID_MOCK, actual.getAttachment().getId());
        assertEquals(ATTACHMENT_TYPE_MOCK, actual.getAttachment().getType());
        assertEquals(ATTACHMENT_NAME_MOCK, actual.getAttachment().getName());
        assertEquals(ATTACHMENT_CONTENT_MOCK, actual.getAttachment().getContent());
    }

    @Test
    void getWrongAttachmentByIdTest() {
        // Arrange
        Attachment attachmentClassMock = new Attachment();
        attachmentClassMock.setId(ATTACHMENT_ID_MOCK);
        attachmentClassMock.setType(ATTACHMENT_TYPE_MOCK);
        attachmentClassMock.setName(ATTACHMENT_NAME_MOCK);
        attachmentClassMock.setContent(ATTACHMENT_CONTENT_MOCK);

        when(messageManager.getAttachment(anyString())).thenReturn(attachmentClassMock);

        // Act
        AttachmentResponse actual = sut.getAttachmentById(ATTACHMENT_ID_MOCK);

        // Assert
        assertNotEquals("wrong id", actual.getAttachment().getId());
        assertNotEquals("wrong type", actual.getAttachment().getType());
        assertNotEquals("wrong name", actual.getAttachment().getName());
        assertNotEquals("wrong content", actual.getAttachment().getContent());
    }

    @Test
    void deleteTimedMessagesTest() {
        // Act & Arrange
        sut.deleteTimedMessages();

        // Assert
        verify(messageManager).deleteTimedMessages();
    }

    @Test
    void getNotificationsTest() {
        // Arrange
        User user = new User();
        user.setId("6");

        Long longMock = 123L;
        ArrayList<String> notificationStrings = new ArrayList<>();
        String stringMock = "notification";
        notificationStrings.add(stringMock);
        ArrayList<Notification> notifications = new ArrayList<>();
        Notification notification = new Notification(stringMock, longMock);
        notifications.add(notification);
        NotificationResponse expected = new NotificationResponse(notifications);

        when(messageManager.getContactsWithNotification(anyString())).thenReturn(notificationStrings);
        when(messageManager.getUnreadMessages(anyString(), anyString())).thenReturn(longMock);

        // Act
        NotificationResponse actual = sut.getNotifications(user);

        // Assert
        assertEquals(expected.getNotifications().get(0).getUserID(), actual.getNotifications().get(0).getUserID());
        assertEquals(expected.getNotifications().get(0).getUnreadCount(), actual.getNotifications().get(0).getUnreadCount());
    }

    @Test
    void getNotificationsNoReadMessagesTest() {
        // Arrange
        User user = new User();
        user.setId("6");

        Long longMock = 0L;
        ArrayList<String> notificationStrings = new ArrayList<>();
        String stringMock = "notification";
        notificationStrings.add(stringMock);
        ArrayList<Notification> notifications = new ArrayList<>();
        NotificationResponse expected = new NotificationResponse(notifications);

        when(messageManager.getContactsWithNotification(anyString())).thenReturn(notificationStrings);
        when(messageManager.getUnreadMessages(anyString(), anyString())).thenReturn(longMock);

        // Act
        NotificationResponse actual = sut.getNotifications(user);

        // Assert
        assertEquals(expected.getNotifications(), actual.getNotifications());
    }

    @Test
    void readChatTest() {
        // Arrange
        String senderIDMock = "1";
        String receiverIDMock = "2";

        // Act
        sut.readChat(senderIDMock, receiverIDMock);

        // Assert
        verify(messageManager).readChat(eq(senderIDMock), eq(receiverIDMock), anyString());
    }

    @Test
    void deleteMessageTest() {
        // Arrange
        ChatMessagesResponse cmr = new ChatMessagesResponse();
        cmr.setMessages(MESSAGES_MOCK);

        when(sut.checkIfUserIsSender(anyString(), anyInt())).thenReturn(true);
        doNothing().when(messageManager).deleteMessage(anyInt());
        doReturn(cmr).when(sut).getChatHistory(anyString(), anyString());

        // Act
        sut.deleteMessage(anyString(), anyString(), anyInt());

        // Assert
        verify(messageManager).deleteMessage(anyInt());
    }

    @Test
    void deleteMessageThrowUserNotOwnerOfMessageExceptionTest() {
        // Arrange
        when(sut.checkIfUserIsSender(anyString(), anyInt())).thenReturn(false);

        // Assert & Act
        assertThrows(UserNotOwnerOfMessageException.class, () -> sut.deleteMessage(anyString(), anyString(), anyInt()));
    }

    @Test
    void checkIfUserIsSenderTest() {
        // Arrange
        when(messageManager.checkMessageFromUser(anyString(), anyInt())).thenReturn(true);

        // Act
        boolean res = sut.checkIfUserIsSender(anyString(), anyInt());

        // Assert
        assertEquals(true, res);
    }

    @Test
    void checkIfUserIsNotSenderTest() {
        // Arrange
        when(messageManager.checkMessageFromUser(anyString(), anyInt())).thenReturn(false);

        // Act
        boolean res = sut.checkIfUserIsSender(anyString(), anyInt());

        // Assert
        assertEquals(false, res);
    }

    @Test
    void getBroadcastsTest() {
        // Assemble
        final String PUBLIC_KEY = "a key";

        List<Broadcast> broadcasts = new ArrayList<>(
                Arrays.asList(
                        new Broadcast(new Message(), PUBLIC_KEY),
                        new Broadcast(new Message(), PUBLIC_KEY),
                        new Broadcast(new Message(), PUBLIC_KEY),
                        new Broadcast(new Message(), PUBLIC_KEY)
                )
        );
        User user = new User();
        user.setId(USER_1_MOCK);
        user.setPublicKey(PUBLIC_KEY);

        when(messageManager.getBroadcasts(anyString())).thenReturn(MESSAGES_MOCK);
        when(userManager.getUser(any())).thenReturn(user);

        // Act
        BroadcastResponse actual = sut.getBroadcasts(USER_1_MOCK);

        // Assert
        assertEquals(broadcasts.size(), actual.getMessages().size());
    }
}
