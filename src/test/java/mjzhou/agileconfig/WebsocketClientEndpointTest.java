package mjzhou.agileconfig;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class WebsocketClientEndpointTest {

    @Test
    void connect() {
        Options options = new Options("http://agileconfig_server.xbaby.xyz", "test_app", "test_app", "");
        WebsocketClientEndpoint endpoint = new WebsocketClientEndpoint(options);
        endpoint.connect();
    }
}