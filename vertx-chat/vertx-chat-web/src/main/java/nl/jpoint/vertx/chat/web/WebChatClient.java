package nl.jpoint.vertx.chat.web;

import nl.jpoint.vertx.chat.client.ChatClient;

public class WebChatClient extends ChatClient {

    public WebChatClient(String handle) {
        super(handle);
    }

    @Override
    public void start() throws Exception {
        super.start();

        // TODO: start sending some chat messages to the chat endpoint
        // Hint: look at the helper methods in the super class
    }
}

