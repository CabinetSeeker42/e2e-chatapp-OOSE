package oose.euphoria.backend.presentation.dto.messages.responses;

import lombok.Getter;
import lombok.Setter;
import oose.euphoria.backend.data.entities.User;

@Getter
@Setter
@SuppressWarnings("java:S1068")
public class RegisterResponse {
    private String type = "REGISTER_SUCCESS";
    private User user;

    /**
     * Constructor for RegisterResponse
     *
     * @param user the user that is registered
     */
    public RegisterResponse(User user) {
        this.user = user;
    }
}
