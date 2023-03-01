package oose.euphoria.backend.presentation.dto.messages.responses;

import lombok.Getter;
import lombok.Setter;
import oose.euphoria.backend.data.entities.Attachment;

@Getter
@Setter
@SuppressWarnings("java:S1068")
public class AttachmentResponse {
    private String type = "ATTACHMENT";
    private Attachment attachment;

    public AttachmentResponse(Attachment attachment) {
        this.attachment = attachment;
    }
}
