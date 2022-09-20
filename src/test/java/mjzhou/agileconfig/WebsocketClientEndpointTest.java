package mjzhou.agileconfig;

import mjzhou.agileconfig.websocket.WebsocketClientEndpoint;
import org.junit.jupiter.api.Test;

class WebsocketClientEndpointTest {

    @Test
    void connect() {
        Options options = new Options("http://agileconfig-server.xbaby.xyz", "test_app", "test_app", "");
        WebsocketClientEndpoint endpoint = new WebsocketClientEndpoint(options.getAppId(), options.getSecret(), options.getEnv(), options.getNodes()[0]);
        endpoint.connect();
    }

    @Test
    void sendMessage() {
        Options options = new Options("http://agileconfig-server.xbaby.xyz", "test_app", "test_app", "");
        WebsocketClientEndpoint endpoint = new WebsocketClientEndpoint(options.getAppId(), options.getSecret(), options.getEnv(), options.getNodes()[0]);
        endpoint.connect();
        endpoint.sendMessage("ping");
    }
}