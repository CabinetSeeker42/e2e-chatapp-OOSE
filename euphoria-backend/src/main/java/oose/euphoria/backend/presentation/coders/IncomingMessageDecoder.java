package oose.euphoria.backend.presentation.coders;

import com.google.gson.Gson;
import oose.euphoria.backend.presentation.dto.messages.MessageRequest;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class IncomingMessageDecoder implements Decoder.Text<MessageRequest> {

    private static Gson gson = new Gson();

    /**
     * Decodes incoming message string to MessageRequest
     *
     * @param message GSON message that should be decoded to object
     * @return Incoming message that was decoded from messsage GSON
     */
    @Override
    public MessageRequest decode(String message) {
        return gson.fromJson(message, MessageRequest.class);
    }

    /**
     * Returns true if the incoming message is not null
     *
     * @param s Incoming message
     * @return True if the incoming message is not null
     */
    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Do nothing because of later implementation for custom initialization logic
    }

    @Override
    public void destroy() {
        // Do nothing because of later implementation for closing resources
    }
}
