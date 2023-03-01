package oose.euphoria.backend.presentation.dto.messages.responses;

import lombok.Getter;
import lombok.Setter;
import oose.euphoria.backend.data.entities.User;

import java.util.List;

@Getter
@Setter
@SuppressWarnings("java:S1068")
public class ContactResponse {
    private String type = "CONTACTS";
    private List<User> users;
    private List<String> supportCompanies;

    public ContactResponse(List<User> users, List<String> supportCompanies) {
        this.users = users;
        this.supportCompanies = supportCompanies;
    }
}
