package mjzhou.agileconfig.websocket;

import javax.websocket.ClientEndpointConfig;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class WebsocketClientConfig extends ClientEndpointConfig.Configurator{
    private String appId;
    private String secret;
    private String env;

    public WebsocketClientConfig(String appId, String secret, String env){
        this.appId = appId;
        this.secret = secret;
        this.env = env;
    }
    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        String encoded = Base64.getEncoder().encodeToString((appId+":"+secret).getBytes(StandardCharsets.UTF_8));
        headers.put("appid", Arrays.asList(appId));
        headers.put("Authorization", Arrays.asList("Basic " + encoded));
        headers.put("env", Arrays.asList(env));
        headers.put("client-v", Arrays.asList("1.6.0"));
    }
}
