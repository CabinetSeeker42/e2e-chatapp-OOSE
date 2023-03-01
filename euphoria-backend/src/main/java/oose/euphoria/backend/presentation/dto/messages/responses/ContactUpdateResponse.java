package oose.euphoria.backend.presentation.dto.messages.responses;

import lombok.Getter;
import oose.euphoria.backend.data.entities.User;
import org.checkerframework.checker.index.qual.SearchIndexBottom;

@Getter
@SearchIndexBottom
public class ContactUpdateResponse {
    private String type = "CONTACT_UPDATE";
    private User user;

    public ContactUpdateResponse(User user) {
        this.user = user;
    }
}
