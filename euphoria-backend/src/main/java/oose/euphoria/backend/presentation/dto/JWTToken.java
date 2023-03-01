package oose.euphoria.backend.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@SuppressWarnings("java:S1068")
public class JWTToken {
    private JDIDetails details;

    //	This is done so the token can be decoded.
    @Getter
    @Setter
    public static class JDIDetails {
        private String userId; // This is userId from the JWTToken. Not ID as normal
        private String accountName;
        private List<String> supportIDs;
    }
}
