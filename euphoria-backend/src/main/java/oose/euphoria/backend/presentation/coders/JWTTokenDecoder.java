package oose.euphoria.backend.presentation.coders;

import com.google.gson.Gson;
import oose.euphoria.backend.exceptions.JWTTokenDecoderException;
import oose.euphoria.backend.presentation.dto.JWTToken;

import java.util.Base64;

public class JWTTokenDecoder {
    private static Gson gson = new Gson();

    protected JWTTokenDecoder() {
    }

    /**
     * Decodes a JWT token into a JWTToken object.
     *
     * @param jwtToken The JWT token to decode
     * @return The decoded JWTToken object
     */
    public static JWTToken decodeJWTToken(String jwtToken) {
        try {
            String[] chunks = jwtToken.split("\\.");

            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            return gson.fromJson(payload, JWTToken.class);
        } catch (Exception e) {
            throw new JWTTokenDecoderException(e);
        }
    }
}
