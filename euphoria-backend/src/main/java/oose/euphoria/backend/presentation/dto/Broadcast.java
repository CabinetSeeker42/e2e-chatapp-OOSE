package oose.euphoria.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import oose.euphoria.backend.data.entities.Message;


@Getter
@Setter
@AllArgsConstructor
public class Broadcast {
    private Message message;
    private String publicKey;
}
