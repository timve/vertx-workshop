package nl.jpoint.vertx.chat.web;

import nl.jpoint.vertx.chat.api.AbstractChatClient;

public class WebChatClient extends AbstractChatClient {

    public WebChatClient(String handle) {
        super(handle);
    }

    @Override
    public void start() throws Exception {
        // TODO: Retrieve key from shared data and create a token using createToken() (inherited from the super class).

        // TODO: start sending some chat messages to the chat endpoint (address is provided by CHANNEL in the superclass).
    }
}

