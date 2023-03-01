package oose.euphoria.backend.presentation;

import oose.euphoria.backend.data.entities.User;
import oose.euphoria.backend.exceptions.MessageContentMissingException;
import oose.euphoria.backend.exceptions.NotLoggedInException;
import oose.euphoria.backend.exceptions.mappers.ExceptionMapper;
import oose.euphoria.backend.presentation.annotations.OnMessage;
import oose.euphoria.backend.presentation.dto.Challenge;
import oose.euphoria.backend.presentation.dto.JDISession;
import oose.euphoria.backend.presentation.dto.messages.MessageRequest;
import oose.euphoria.backend.presentation.dto.messages.responses.*;
import oose.euphoria.backend.presentation.dto.messages.staticmessage.GeneratorMessage;
import oose.euphoria.backend.service.IMessageService;
import oose.euphoria.backend.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Component
public class WsController {
    private static final int MAX_BUFFER_SIZE = 33554432; // 32 MB
    @Autowired
    private IMessageService messageService;
    @Autowired
    private IUserService userService;
    private HashMap<Session, JDISession> sessions = new HashMap<>();

    /**
     * Gets a JDISession by session.
     *
     * @param session the session Key
     * @return JDISession store
     */
    public JDISession getSession(Session session) {
        return sessions.get(session);
    }

    /**
     * Starts a session. Creates a new JDISession store and sets the userID and session. Send a generator to the session.
     *
     * @param session The session that runs the flow
     * @param userID  The userID that starts a session
     */
    public void startSession(Session session, String userID) {
        sessions.put(session, new JDISession());
        getSession(session).setUserID(userID);
        getSession(session).setSession(session);
        session.setMaxTextMessageBufferSize(MAX_BUFFER_SIZE);

        sendMessageToSession(session, new GeneratorMessage());
    }

    /**
     * Runs onClose code event code.
     *
     * @param session The session that needs to be removed
     */
    public void onClose(Session session) {
        sessions.remove(session);
    }

    /**
     * Throws an exception if the user is not logged in.
     */
    public void checkSessionLoggedIn(Session session) {
        if (!getSession(session).isLoggedIn()) throw new NotLoggedInException();
    }

    /**
     * Runs the SOLVE_CHALLENGE type flow.
     *
     * @param session The session that runs the flow
     * @param message The messagerequest
     */
    @OnMessage(type = "SOLVE_CHALLENGE", login = false)
    public void solveChallenge(Session session, MessageRequest message) {
        userService.checkChallenge(message.getChallengeSolution(), getSession(session).getSharedKey(), getSession(session).getChallengeSolution());
        sendMessageToSession(session, new LoginAcceptedResponse(userService.getUser(message.getSenderID())));
        sendMessageToUserID(message.getSenderID(), null, messageService.getNotifications(userService.getUser(message.getSenderID())));

        setLoggedIn(session);
    }

    /**
     * Sets the session loggedIn to true
     *
     * @param session The session that should be logged in
     */
    public void setLoggedIn(Session session) {
        getSession(session).setLoggedIn(true);
    }

    /**
     * Runs the LOGIN type flow.
     *
     * @param session The session that runs the flow
     * @param message The messagerequest
     */
    @OnMessage(type = "LOGIN", login = false)
    public void login(Session session, MessageRequest message) {
        Challenge challenge = userService.generateChallenge(message.getSenderID());
        getSession(session).setChallengeSolution(challenge.getChallengeSolved());
        getSession(session).setSharedKey(challenge.getSharedKey());

        sendMessageToSession(session, new ChallengeResponse(challenge.getChallengeEncrypted(), challenge.getPublicKey()));
    }

    /**
     * Runs the REGISTER type flow.
     *
     * @param session The session that runs the flow
     * @param message The messagerequest
     */
    @OnMessage(type = "REGISTER", login = false)
    public void register(Session session, MessageRequest message) {
        RegisterResponse registerResponse = userService.registerUser(message.getJwtToken(), message.getPublicKey(), message.getCompanyID(), message.isSupport());
        sendMessageToSession(session, registerResponse);
        updateContactList(message.getSenderID());
    }

    /**
     * Runs the DELETE_MESSAGE type flow.
     *
     * @param session The session that runs the flow
     * @param message The messagerequest
     */
    @OnMessage(type = "DELETE_MESSAGE")
    public void deleteMessage(Session session, MessageRequest message) {
        String lastReceiverID = getSession(session).getLastReceiverID();
        ChatMessagesResponse newChatMessages = messageService.deleteMessage(lastReceiverID, message.getSenderID(), message.getMessageID());
        sendMessageToUserID(message.getSenderID(), lastReceiverID, newChatMessages);
        sendMessageToUserID(lastReceiverID, message.getSenderID(), newChatMessages);
        sendMessageToUserID(message.getSenderID(), null, messageService.getNotifications(userService.getUser(message.getSenderID())));
        sendMessageToUserID(lastReceiverID, null, messageService.getNotifications(userService.getUser(lastReceiverID)));
    }

    /**
     * Runs the BROADCAST_MESSAGE type flow.
     *
     * @param session The session that runs the flow
     * @param message The messagerequest
     */
    @OnMessage(type = "BROADCAST_MESSAGE")
    public void broadcastMessage(Session session, MessageRequest message) {
        messageService.saveMessage(message.getContent(),
                message.getSenderID(),
                message.getReceiverID(),
                message.getAttachmentType(),
                message.getAttachmentName(),
                message.getAttachmentContent(),
                true);
        BroadcastResponse broadcastResponse = messageService.getBroadcasts(message.getReceiverID());
        sendMessageToUserID(message.getReceiverID(), null, broadcastResponse);
    }

    /**
     * Runs the BROADCAST_MESSAGE type flow.
     *
     * @param session The session that runs the flow
     * @param message The messagerequest
     */
    @OnMessage(type = "GET_BROADCASTS")
    public void getBroadcasts(Session session, MessageRequest message) {
        BroadcastResponse broadcastResponse = messageService.getBroadcasts(message.getSenderID());
        sendMessageToSession(session, broadcastResponse);
    }

    /**
     * Runs the READ_CHAT type flow.
     *
     * @param session The session that runs the flow
     * @param message The messagerequest
     */
    @OnMessage(type = "READ_CHAT")
    public void readChat(Session session, MessageRequest message) {
        messageService.readChat(message.getSenderID(), message.getReceiverID());

        sendMessageToSession(session, messageService.getNotifications(userService.getUser(message.getSenderID())));
    }

    /**
     * Runs the GET_CONTACTS type flow.
     *
     * @param session The session that runs the flow
     * @param message The messagerequest
     */
    @OnMessage(type = "GET_CONTACTS")
    public void getContacts(Session session, MessageRequest message) {
        List<User> users = userService.getUsersInContact(message.getSenderID(), userService.getUser(message.getSenderID()).getCompanyID());
        sendMessageToSession(session, new ContactResponse(users, userService.getUserSupportCompanies(message.getSenderID())));
    }

    /**
     * Runs the GET_MESSAGES type flow.
     *
     * @param session The session that runs the flow
     * @param message The messagerequest
     */
    @OnMessage(type = "GET_MESSAGES")
    public void getMessages(Session session, MessageRequest message) {
        ChatMessagesResponse chatMessages = messageService.getChatHistory(message.getSenderID(), message.getReceiverID());

        sendMessageToSession(session, chatMessages);
    }

    /**
     * Runs the MESSAGE type flow.
     *
     * @param session The session that runs the flow
     * @param message The messagerequest
     */
    @OnMessage(type = "MESSAGE")
    public void message(Session session, MessageRequest message) {
        MessageResponse chatMessage = messageService.saveMessage(message.getContent(), message.getSenderID(), message.getReceiverID(), message.getAttachmentType(), message.getAttachmentName(), message.getAttachmentContent(), false);

        sessions.values()
                .forEach(jdiSession -> {
                    if (jdiSession.getLastReceiverID().equals(message.getSenderID())) {
                        messageService.readChat(message.getReceiverID(), message.getSenderID());
                        return;
                    }
                });

        NotificationResponse chatNotification = messageService.getNotifications(userService.getUser(message.getReceiverID()));

        sendChatMessageToUserID(message.getReceiverID(), chatMessage, chatNotification);
        sendMessageToUserID(message.getSenderID(), message.getReceiverID(), chatMessage);
    }

    /**
     * Runs the GET_ATTACHMENT type flow.
     *
     * @param session The session that runs the flow
     * @param message The messagerequest
     */
    @OnMessage(type = "GET_ATTACHMENT")
    public void getAttachment(Session session, MessageRequest message) {
        AttachmentResponse attachment = messageService.getAttachmentById(message.getAttachmentID());
        sendMessageToSession(session, attachment);
    }


    /**
     * Sets the message senderID to the userID of the session and checks if the type is filled.
     *
     * @param session The session of the main message
     * @param message The incoming message
     */
    public void onMessage(Session session, MessageRequest message) {
        message.setSenderID(getSession(session).getUserID());
        if (message.getType() == null) {
            throw new MessageContentMissingException("type");
        }
        if (message.getReceiverID() != null) {
            getSession(session).setLastReceiverID(message.getReceiverID());
        }
    }

    /**
     * Sends exception error message to session.
     *
     * @param session Session the message should be sent to
     * @param e       The exception that should be sent
     */
    public void sendError(Session session, Exception e) {
        sendMessageToSession(session, ExceptionMapper.parse((Exception) e.getCause()));
    }

    /**
     * Sends message to session remote.
     *
     * @param session The session to get the remote from
     * @param message The message that should be sent to session remote
     */
    public void sendMessageToSession(Session session, Object message) {
        try {
            session.getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException e) { // NOSONAR dont make this smell, we want to stacktrace this.
            e.printStackTrace(); // NOSONAR dont make this smell, we want to stacktrace this.
        }
    }

    /**
     * Sends a specific user a message. If a user has multiple instances, all of them get the message.
     *
     * @param receiverID UserID of the receiver the message should be sent to
     * @param message    The message Object the receiver should receive
     */
    public void sendMessageToUserID(String receiverID, String lastReceiverID, Object message) {
        sessions.keySet()
                .stream()
                .filter(session -> getSession(session).getUserID().equals(receiverID))
                .filter(session -> (lastReceiverID == null || getSession(session).getLastReceiverID().equals(lastReceiverID)))
                .forEach(session -> sendMessageToSession(session, message));
    }

    /**
     * Send message to remote if the last receiverID is the current senderID, otherwise send a notificationMessage. This was we can allow multiple instances.
     *
     * @param receiverID   The receiverID of the message
     * @param chatMessage  The message
     * @param notification The notification if the user is not in their chat
     */
    public void sendChatMessageToUserID(String receiverID, MessageResponse chatMessage, NotificationResponse notification) {
        sessions
                .keySet()
                .stream()
                .forEach(session -> {
                    if (getSession(session).getUserID().equals(receiverID)) {
                        if (getSession(session).getLastReceiverID().equals(chatMessage.getMessage().getSenderID())) {
                            sendMessageToSession(session, chatMessage);
                        } else {
                            sendMessageToSession(session, notification);
                        }
                    }
                });
    }

    /**
     * Updates all users in this list with the new contactlist. The user it gets send to will be removed from this list before its sent.
     *
     * @param userID The users that should get their list updated
     */
    public void updateContactList(String userID) {
        User user = userService.getUser(userID);
        userService.getUsersInContact(userID, user.getCompanyID())
                .forEach(contact -> sendMessageToUserID(contact.getId(), null, new ContactUpdateResponse(user)));
    }
}
