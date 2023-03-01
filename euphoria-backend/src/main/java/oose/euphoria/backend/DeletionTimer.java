package oose.euphoria.backend;

import oose.euphoria.backend.configuration.SpringContext;
import oose.euphoria.backend.service.IMessageService;
import oose.euphoria.backend.service.MessageService;

import java.util.TimerTask;

class DeletionTimer extends TimerTask {
    IMessageService messageService;

    public DeletionTimer() {
        messageService = SpringContext.getBean(MessageService.class);
    }

    @Override
    public void run() {
        messageService.deleteTimedMessages();
    }
}
