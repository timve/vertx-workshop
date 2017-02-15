package nl.jpoint.vertx.chat.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verticle to provide the SockJS event bus bridge.
 */
public class SockJSEventBusBridge extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(SockJSEventBusBridge.class);

    @Override
    public void start() {
        LOG.info("started");
        configureEventBusBrige();
    }

    private void configureEventBusBrige() {
        Router router = Router.router(vertx);

        BridgeOptions options = new BridgeOptions()
                .setReplyTimeout(2500)
                .addOutboundPermitted(new PermittedOptions().setAddress("general"))
                .addInboundPermitted(new PermittedOptions().setAddress("general"));

        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(options));

        router.route().handler(StaticHandler.create("web"));

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

}
