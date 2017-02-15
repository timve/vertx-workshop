package nl.jpoint.vertx.chat.web;

import nl.jpoint.vertx.chat.api.AbstractVertxStarter;

import java.net.SocketException;

public class WebStarter extends AbstractVertxStarter {

    public static void main(String... args) throws SocketException {
        if (args.length != 1) {
            throw new IllegalArgumentException("client handle should be provided at startup.");
        }

        deployVerticle(new SockJSEventBusBridge());
        deployVerticle(new WebChatClient(args[0]));
    }

}
