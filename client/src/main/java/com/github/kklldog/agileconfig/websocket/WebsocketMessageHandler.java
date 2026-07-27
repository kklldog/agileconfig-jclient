package com.github.kklldog.agileconfig.websocket;

import com.github.kklldog.agileconfig.DefaultJsonConvert;
import com.github.kklldog.agileconfig.IConfigClient;
import com.github.kklldog.agileconfig.IJsonConvert;
import com.github.kklldog.agileconfig.Protocol;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebsocketMessageHandler implements WebsocketClientEndpoint.MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketMessageHandler.class);

    private final IJsonConvert jsonConvert = new DefaultJsonConvert();
    private final IConfigClient configClient;

    public WebsocketMessageHandler(IConfigClient client) {
        configClient = client;
    }

    @Override
    public void handleMessage(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        // 老版本服务端的心跳回复, 直接丢弃
        if ("0".equals(message)) {
            return;
        }

        // 老版本服务端的 ping 回复, 格式为 V:{version}
        if (message.startsWith("V:")) {
            String serverVersion = message.substring(2);
            if (!serverVersion.equals(configClient.md5Version())) {
                configClient.load();
            }
            return;
        }

        ActionMessage act = jsonConvert.deserializeObject(message, new TypeReference<ActionMessage>() {
        });
        if (act == null) {
            return;
        }

        // module 为空的是老版本的配置中心消息, 一样按 action 处理
        String module = act.getModule();
        if (module == null || module.isEmpty() || module.equals(Protocol.ActionModule.ConfigCenter)) {
            handleAction(act);
        }
    }

    private void handleAction(ActionMessage act) {
        String action = act.getAction();
        if (action == null) {
            return;
        }

        try {
            switch (action) {
                case Protocol.ActionConst.Reload:
                    configClient.load();
                    break;
                case Protocol.ActionConst.Offline:
                    configClient.disconnect();
                    break;
                case Protocol.ActionConst.Ping:
                    String serverVersion = act.getData();
                    String localVersion = configClient.localVersion();
                    if (serverVersion != null && !serverVersion.equals(localVersion)) {
                        logger.trace(String.format("the local configs version %s is different from server %s , " +
                                "try to pull all configs from server .", localVersion, serverVersion));
                        configClient.load();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error(String.format("cannot handle websocket action , Module: %s Action: %s Data: %s",
                    act.getModule(), act.getAction(), act.getData()), e);
        }
    }
}
