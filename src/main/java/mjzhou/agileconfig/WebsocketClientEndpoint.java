package mjzhou.agileconfig;

import java.net.URI;
import java.nio.ByteBuffer;
import javax.websocket.*;

/**
 * ChatServer Client
 * copy from https://stackoverflow.com/questions/26452903/javax-websocket-client-simple-example
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
public class WebsocketClientEndpoint extends Endpoint {

    Session userSession = null;
    private MessageHandler messageHandler;

    private WebSocketContainer webSocketContainer;

    private Options options;

    public WebsocketClientEndpoint(Options options) {
        this.options = options;
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket 0");
        this.userSession = userSession;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("opening websocket 1");
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket");
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    @OnMessage
    public void onMessage(ByteBuffer bytes) {
        System.out.println("Handle byte buffer");
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
        this.userSession.getAsyncRemote().sendText(message);
    }

    /**
     * Message handler.
     *
     * @author Jiji_Sasidharan
     */
    public static interface MessageHandler {

        public void handleMessage(String message);
    }
}
