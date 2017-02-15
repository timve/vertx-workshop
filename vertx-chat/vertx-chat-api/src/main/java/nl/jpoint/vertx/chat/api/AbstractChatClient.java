package nl.jpoint.vertx.chat.api;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Abstract base class for creating chat client verticles
 */
public abstract class AbstractChatClient extends io.vertx.rxjava.core.AbstractVerticle {

    /**
     * Key for the message header value containing the client handle.
     */
    public static final String MESSAGE_HEADER_HANDLE = "handle";

    /**
     * The global chat channel endpoint address.
     */
    protected static final String CHANNEL = "general";

    /**
     * The key for the global map containing the secret key.
     */
    protected static final String SECRETS_MAP = "chat";

    /**
     * The map key for the secret key to generate a signature.
     */
    protected static final String SECRETS_MAP_KEY = "key";

    /**
     * The handle for this client.
     */
    protected String handle;

    /**
     * Create a new chat client with the given handle.
     * @param handle The name for this client
     */
    public AbstractChatClient(String handle) {
        this.handle = handle;
    }

    public AbstractChatClient() {
    }

    @Override
    public abstract void start() throws Exception;

    /**
     * Generate a security signature based on the provided key and the chat client handle.
     * @param key the secret key.
     * @param handle the chat client handle.
     */
    protected String createToken(String key, String handle) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"),
                    "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSha256");
            mac.init(secretKey);
            byte[] bytes = mac.doFinal(handle.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
