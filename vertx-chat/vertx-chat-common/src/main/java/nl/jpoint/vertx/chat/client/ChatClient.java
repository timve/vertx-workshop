package nl.jpoint.vertx.chat.client;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.rxjava.core.eventbus.Message;
import nl.jpoint.vertx.chat.api.AbstractChatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscription;
import rx.functions.Action1;

public class ChatClient extends AbstractChatClient {

    private static Logger LOG = LoggerFactory.getLogger(ChatClient.class);

    /**
     * The token used to sign the handle of the sender for the message.
     */
    protected String token;

    /**
     * Creates a new {@code ChatClient} with the provided handle
     * @param handle The handle to use when sending messages
     */
    public ChatClient(String handle) {
        super(handle);
    }

    @Override
    public void start() throws Exception {
    }

    /**
     * Sends the message to the channel.
     * @param channel The channel to send the message to.
     * @param message The message to send to the channel.
     */
    protected void sendMessage(String channel, String message) {
        LOG.debug("Sending message '{}' to channel {}", message, channel);
        vertx.eventBus().publish(channel, message, createDeliveryOptions());
    }

    /**
     * Subscribes to a channel invoking consumer on every received message.
     * @param channel The channel to start receiving messages from.
     * @param consumer The action to take on reveiving a message.
     * @return The subscription to the channel.
     */
    private Subscription subscribe(String channel, Action1<Message<Object>> consumer) {
        LOG.info("Subscribing {} to channel {}", handle, channel);
        return vertx.eventBus().consumer(channel).toObservable().subscribe(consumer);
    }

    /**
     * Function to process an incoming message from a channel.
     * @param channel The channel the message was received on.
     * @param message The message that was received.
     */
    protected void processMessage(String channel, Message<Object> message) {
        String sender = message.headers().get("handle");
        System.out.print(String.format("[%s:%s] %s\n", channel, sender, message.body()));
    }

    /**
     * Create a {@code DeliveryOptions} object to provide chat headers to be sent along with a message.
     * @return The newly created DeliveryOptions containing the headers.
     */
    protected DeliveryOptions createDeliveryOptions() {
        DeliveryOptions options = new DeliveryOptions().addHeader("handle", handle);
        if (token != null) {
            options.addHeader("token", token);
        } else {
            System.out.println("WARNING: signature token is not set");
        }
        return options;
    }
}
