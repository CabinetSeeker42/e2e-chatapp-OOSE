package oose.euphoria.backend.presentation.dto.messages.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("java:S1068")
public class ChallengeResponse {
    private String type = "CHALLENGE";
    private String challenge;
    private String serverPublicKey;

    public ChallengeResponse(String challenge, String serverPublicKey) {
        this.challenge = challenge;
        this.serverPublicKey = serverPublicKey;
    }
}
