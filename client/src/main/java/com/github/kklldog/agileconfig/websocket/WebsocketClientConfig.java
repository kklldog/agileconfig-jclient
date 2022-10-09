package com.github.kklldog.agileconfig.websocket;

import javax.websocket.ClientEndpointConfig;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebsocketClientConfig extends ClientEndpointConfig.Configurator {
    private final String appId;
    private final String secret;
    private final String env;

    public WebsocketClientConfig(String appId, String secret, String env) {
        this.appId = appId;
        this.secret = secret;
        this.env = env;
    }

    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        String encoded = Base64.getEncoder().encodeToString((appId + ":" + secret).getBytes(StandardCharsets.UTF_8));
        headers.put("appid", Collections.singletonList(appId));
        headers.put("Authorization", Collections.singletonList("Basic " + encoded));
        headers.put("env", Collections.singletonList(env));
        headers.put("client-v", Collections.singletonList("1.6.0"));
    }
}
