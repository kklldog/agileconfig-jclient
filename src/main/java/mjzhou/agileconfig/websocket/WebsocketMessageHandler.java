package mjzhou.agileconfig.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import mjzhou.agileconfig.DefaultJsonConvert;
import mjzhou.agileconfig.IConfigClient;
import mjzhou.agileconfig.IJsonConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebsocketMessageHandler implements WebsocketClientEndpoint.MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketMessageHandler.class);

    private IJsonConvert jsonConvert = new DefaultJsonConvert();
    private IConfigClient configClient;

    public WebsocketMessageHandler(IConfigClient client) {
        configClient = client;
    }

    @Override
    public void handleMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        ActionMessage act = jsonConvert.deserializeObject(message, new TypeReference<ActionMessage>() {
        });
        if (act == null) {
            return;
        }

        if (act.getModule().equals("c")) { // c = config center
            String action = act.getAction();
            switch (action) {
                case "reload":
                    configClient.load();
                    break;
                case "offline":
                    configClient.disconnect();
                    break;
                case "ping":
                    String serverVersion = act.getData();
                    String localVersion = configClient.md5Version();
                    if (!serverVersion.equals(localVersion)) {
                        logger.trace(String.format("the local configs version %s is different from server %s , " +
                                "try to pull all configs from server .",localVersion, serverVersion));
                        configClient.load();
                    }
                    break;
                default:
                    break;
            }
        }

    }
}
