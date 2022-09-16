package mjzhou.agileconfig.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import mjzhou.agileconfig.DefaultJsonConvert;
import mjzhou.agileconfig.IConfigClient;
import mjzhou.agileconfig.IJsonConvert;

public class WebsocketMessageHandler implements WebsocketClientEndpoint.MessageHandler {
    IJsonConvert jsonConvert = new DefaultJsonConvert();
    IConfigClient configClient;

    public WebsocketMessageHandler(IConfigClient client){
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
                    break;
                default:
                    break;
            }
        }

    }
}
