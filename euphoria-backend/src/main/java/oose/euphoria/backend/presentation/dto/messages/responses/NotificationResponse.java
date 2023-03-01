package oose.euphoria.backend.presentation.dto.messages.responses;

import lombok.Getter;
import lombok.Setter;
import oose.euphoria.backend.presentation.dto.Notification;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuppressWarnings("java:S1068")
public class NotificationResponse {
    private String type = "NOTIFICATIONS";
    private List<Notification> notifications = new ArrayList<>();

    public NotificationResponse(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }
}
