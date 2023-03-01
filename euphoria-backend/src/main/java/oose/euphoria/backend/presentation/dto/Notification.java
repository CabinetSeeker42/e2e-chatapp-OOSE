package oose.euphoria.backend.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@SuppressWarnings("java:S1068")
public class Notification {
    private String userID;
    private Long unreadCount;
}
