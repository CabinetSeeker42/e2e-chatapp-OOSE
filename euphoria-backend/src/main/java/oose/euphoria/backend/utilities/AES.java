package oose.euphoria.backend.utilities;

import oose.euphoria.backend.exceptions.LoginRejectedException;
import org.apache.tomcat.util.codec.binary.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class AES {
    private AES() {
    }


    /**
     * @param secret The secret key
     * @param mode   The mode of the cipher
     * @return The cipher
     * @throws NoSuchPaddingException   If the padding is not found
     * @throws NoSuchAlgorithmException If the algorithm is not found
     * @throws InvalidKeyException      If the key is invalid
     */
    private static Cipher createCipher(String secret, int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] secretKey = Arrays.copyOfRange(secret.getBytes(StandardCharsets.UTF_8), 0, 32);
        Key key = new SecretKeySpec(secretKey, "AES");
        Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding"); //NOSONAR. This padding should be changed on both the frontend and backend if the PO wants to.
        c.init(mode, key);
        return c;
    }

    /**
     * Encrypts the data with the key
     *
     * @param message The data that should be encrypted
     * @param secret  The key that should be used to encrypt the data
     * @return The encrypted data in AES
     */
    public static String encrypt(String message, String secret) {
        try {
            Cipher c = createCipher(secret, Cipher.ENCRYPT_MODE);

            byte[] encVal = c.doFinal(StringUtils.getBytesUtf8(message));
            return Base64.getEncoder().encodeToString(encVal);
        } catch (Exception e) {
            throw new LoginRejectedException();
        }
    }

    /**
     * Decrypts the data with the key
     *
     * @param message The data that should be encrypted
     * @param secret  The key that should be used to encrypt the data
     * @return The encrypted data in AES
     */
    public static String decrypt(String message, String secret) {
        try {
            Cipher c = createCipher(secret, Cipher.DECRYPT_MODE);

            byte[] byteArray = Base64.getDecoder().decode(message);
            byte[] decValue = c.doFinal(byteArray);
            return new String(decValue);
        } catch (Exception e) {
            throw new LoginRejectedException();
        }
    }
}
