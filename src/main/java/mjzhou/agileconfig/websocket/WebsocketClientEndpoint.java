package mjzhou.agileconfig.websocket;

import mjzhou.agileconfig.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import javax.websocket.*;

/**
 * websocket Client
 * copy from https://stackoverflow.com/questions/26452903/javax-websocket-client-simple-example
 * @author Jiji_Sasidharan
 */
public class WebsocketClientEndpoint extends Endpoint {
    private static final Logger logger =  LoggerFactory.getLogger(WebsocketClientEndpoint.class);

    public boolean isOpened() {
        return opened;
    }

    private boolean opened;

    private Session userSession = null;
    private MessageHandler messageHandler;

    private WebSocketContainer webSocketContainer;

    private Options options;

    private WebsocketClientEndpoint _this;

    public WebsocketClientEndpoint(Options options) {
        this.options = options;
        this._this = this;
        webSocketContainer = ContainerProvider.getWebSocketContainer();
    }

    public void connect(){
        String server = options.getNodes()[0];
        if (!server.endsWith("/")){
            server += "/";
        }
        server  += "ws";
        if (server.startsWith("http://")){
            server = server.replace("http://","ws://");
        }
        if (server.startsWith("https://")){
            server = server.replace("https://","wss://");
        }
        ClientEndpointConfig.Builder configBuilder = ClientEndpointConfig.Builder.create();
        configBuilder.configurator(new WebsocketClientConfig(options.getAppId(), options.getSecret(), options.getEnv()));
        ClientEndpointConfig clientConfig = configBuilder.build();
        try {
            webSocketContainer.connectToServer(this, clientConfig, new URI(server));
            logger.info(String.format("websocket client connect to %s successful , appId: %s", server, options.getAppId()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect(){
        if (opened) {
            if (userSession != null) {
                try {
                    userSession.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "close by server force"));
                } catch (IOException e) {
                    logger.error("try to close websocket client error", e);
                }
            }
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        logger.info("opening websocket");
        this.opened = true;
        this.userSession = session;
        this.userSession.addMessageHandler(new javax.websocket.MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String s) {
                _this.onMessage(s);
            }
        });
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @Override
    public void onClose(Session userSession, CloseReason reason) {
        logger.info("closing websocket");
        this.opened = false;
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    public void onMessage(String message) {
        logger.info("receive message " + message);
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        if (this.userSession != null) {
            this.userSession.getAsyncRemote().sendText(message);
        }
    }

    /**
     * Message handler.
     *
     */
    public static interface MessageHandler {

        public void handleMessage(String message);
    }
}
