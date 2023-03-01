package oose.euphoria.backend.presentation;

import oose.euphoria.backend.configuration.SpringContext;
import oose.euphoria.backend.exceptions.TypeNotFoundException;
import oose.euphoria.backend.presentation.annotations.OnMessage;
import oose.euphoria.backend.presentation.coders.IncomingMessageDecoder;
import oose.euphoria.backend.presentation.coders.ObjectEncoder;
import oose.euphoria.backend.presentation.dto.messages.MessageRequest;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ServerEndpoint(value = "/chat/{userid}", decoders = IncomingMessageDecoder.class, encoders = {ObjectEncoder.class})
public class WsEventsHandler {
    private WsController wsController = SpringContext.getBean(WsController.class);

    /**
     * Accepts the incoming connection and immediately sends the generator to the user.
     *
     * @param session Current session from the user
     * @param userID  The userID from path
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userid") String userID) {
        wsController.startSession(session, userID);
    }

    /**
     * Handles the incoming message events on the websocket connections.
     * The type of the message determines what will be done with it. Ex: All incoming messages should be saved on the database.
     *
     * @param session Current session the message came from
     * @param message The message in IncomingMessage form
     */
    @javax.websocket.OnMessage
    public void onMessage(Session session, MessageRequest message) {
        try {
            wsController.onMessage(session, message);
            for (Method method : wsController.getClass().getDeclaredMethods()) {
                if (!method.isAnnotationPresent(OnMessage.class) || !method.getAnnotation(OnMessage.class).type().equals(message.getType()))
                    continue;
                if (method.getAnnotation(OnMessage.class).login()) {
                    wsController.checkSessionLoggedIn(session);
                }
                method.invoke(wsController, session, message);
                return;
            }
            throw new TypeNotFoundException(message.getType());
        } catch (InvocationTargetException e) {
            wsController.sendError(session, e);
        } catch (IllegalAccessException e) { // NOSONAR We are stack-tracing this if this happen
            e.printStackTrace(); // NOSONAR. This should never happen, but if it does, we want to see it in our console.
        }
    }

    /**
     * Handles the websocket close events. Removes the endpoint from the saved endpoints.
     *
     * @param session Session that closed the connection
     */
    @OnClose
    public void onClose(Session session) {
        wsController.onClose(session);
    }
}
