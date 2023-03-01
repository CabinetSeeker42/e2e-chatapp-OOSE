package oose.euphoria.backend.presentation.dto.messages.responses;

import lombok.Getter;
import lombok.Setter;
import oose.euphoria.backend.data.entities.User;

@Getter
@Setter
@SuppressWarnings("java:S1068")
public class LoginAcceptedResponse {
    private String type = "LOGIN_ACCEPTED";
    private String message = "Login accepted! Welcome!";
    private User user;

    public LoginAcceptedResponse(User user) {
        this.user = user;
    }
}
