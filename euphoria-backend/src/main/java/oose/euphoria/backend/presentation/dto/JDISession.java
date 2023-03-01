package oose.euphoria.backend.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import javax.websocket.Session;

@Getter
@Setter
@SuppressWarnings("java:S1068")
public class JDISession {
    private String userID;
    private String challengeSolution;
    private String sharedKey;
    private String lastReceiverID = "";
    private Session session;
    private boolean loggedIn = false;
}
