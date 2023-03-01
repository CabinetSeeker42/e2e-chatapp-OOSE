package oose.euphoria.backend.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@SuppressWarnings("java:S1068")
public class Challenge {
    private String challengeEncrypted;
    private String challengeSolved;
    private String publicKey;
    private String sharedKey;

    public Challenge(String challengeEncrypted, String challengeSolved, String publicKey, String sharedKey) {
        this.challengeEncrypted = challengeEncrypted;
        this.challengeSolved = challengeSolved;
        this.publicKey = publicKey;
        this.sharedKey = sharedKey;
    }
}
