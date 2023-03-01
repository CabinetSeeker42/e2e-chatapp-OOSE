package oose.euphoria.backend.presentation.coders;

import com.google.gson.Gson;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class ObjectEncoder implements Encoder.Text<Object> {

    protected static Gson gson = new Gson();

    /**
     * Returns a Gson-encoded string based on the incoming object.
     *
     * @param message The message that should be encoded to GSON
     * @return GSON message (JSON as string)
     */
    @Override
    public String encode(Object message) {
        return gson.toJson(message);
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
