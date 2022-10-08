package com.github.kklldog.agileconfig;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.kklldog.agileconfig.websocket.WebsocketClientEndpoint;
import com.github.kklldog.agileconfig.websocket.WebsocketMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.github.kklldog.agileconfig.Encrypt.md5;

public class ConfigClient implements IConfigClient {
    private static final Logger logger = LoggerFactory.getLogger(ConfigClient.class);

    private static final Comparator<String> stringComparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    };

    private final IJsonConvert jsonConvert = new DefaultJsonConvert();

    private final Options options;
    private Map<String, String> data;
    private final IConfigLoader configLoader;

    private Thread pingThread;
    private Thread reconnectThread;
    private static final int pingInterval = 30;
    private WebsocketClientEndpoint websocketClient;

    public ConfigClient(Options options) {
        configLoader = new DefaultHttpConfigLoader();
        this.options = options;
    }

    @Override
    public Map<String, String> getConfigs() {
        return data;
    }

    @Override
    public String get(String key) {
        if (data == null) {
            return "";
        }

        if (data.containsKey(key)) {
            return data.get(key);
        }

        return "";
    }

    private boolean tryConnect() {
        RandomServers randomServer = new RandomServers(this.options.getNodes());
        while (!randomServer.isComplete()) {
            String node = randomServer.next();
            WebsocketClientEndpoint endpoint = new WebsocketClientEndpoint(this.options.getAppId(), this.options.getSecret(),
                    this.options.getEnv(), node);
            endpoint.addMessageHandler(new WebsocketMessageHandler(this));
            try {
                endpoint.connect();
                this.websocketClient = endpoint;
                logger.info("connect to server " + endpoint.getNodeAddress() + " successful .");
                return true;
            } catch (Exception e) {
                logger.error(String.format("try connect to server %s fail .", node), e);
            }
        }

        logger.info("all the server can not be connected .");

        return false;
    }

    @Override
    public void connect() {
        tryConnect();
        // 不管怎么样，都要拉去配置
        this.load();
        this.startAutoReconnect();
        this.startPing();
    }

    @Override
    public void disconnect() {
        if (websocketClient != null) {
            websocketClient.disconnect();
            websocketClient = null;
            this.pingThread.interrupt();
            this.reconnectThread.interrupt();
        }
    }

    @Override
    public String md5Version() {
        if (data == null) {
            return  "";
        }

        List<String> keys =  this.data.keySet().stream().sorted(stringComparator).collect(Collectors.toList());
        List<String> vals = this.data.values().stream().sorted(stringComparator).collect(Collectors.toList());
        String strKeys = String.join("&", keys);
        String strVals = String.join("&", vals);
        String sourceText = strKeys + "&" + strVals;

        return  md5(sourceText).toUpperCase();
    }

    /**
     * 打开一个新的线程开始定时发送ping消息
     */
    private void startPing() {
        pingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(pingInterval * 1000);
                        if (websocketClient != null && websocketClient.isOpened()) {
                            websocketClient.sendMessage("ping");
                            logger.trace("send ping message to server");
                        }
                    } catch (InterruptedException e) {
                        logger.info("HeartBeat Thread Interrupted .", e);
                    }
                }
            }
        });
        pingThread.start();
    }

    /**
     * 开始尝试重连
     */
    private void startAutoReconnect() {
        reconnectThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(5000);
                    if (websocketClient != null && websocketClient.isOpened()) {
                        continue;
                    }

                    if (tryConnect()) {
                        load();
                    }
                } catch (InterruptedException e) {
                    logger.info("AutoReconnect Thread Interrupted .", e);
                }
            }
        });
        reconnectThread.start();
    }

    @Override
    public void load() {
        String[] nodes = options.getNodes();
        boolean testAll = false;
        RandomServers randomServer = new RandomServers(nodes);

        int idx = 0;
        while (!randomServer.isComplete()) {
            String node = randomServer.next();
            List<ConfigItem> configs = getConfigsFromRemote(node);
            if (configs == null) {
                if (idx == nodes.length - 1) {
                    logger.info(String.format("can NOT get app's configs from all nodes . appid: %s", options.getAppId()));
                    testAll = true;
                    break;
                }
                idx++;
                continue; // cant get configs from node , so try next .
            }

            logger.info(String.format("get app's configs from node success . appid: %s , node: %s", options.getAppId(), node));

            writeConfigsToLocal(configs); // 获取到配置后第一时间写到本地文件

            this.load(configs);

            break;
        }

        if (testAll && this.options.isReloadFromLocal()) {
            // 如果所有的节点尝试获取配置都失败，那么从本地文件恢复
            reloadConfigsFromLocal();
        }
    }

    @Override
    public void load(List<ConfigItem> configs) {
        if (this.data == null) {
            this.data = new ConcurrentHashMap<>();
        } else {
            this.data.clear();
        }

        for (ConfigItem item : configs) {
            String key = "";
            if (item.getGroup().isEmpty()) {
                key = item.getKey();
            } else {
                key = item.getGroup() + ":" + item.getKey();
            }
            data.put(key, item.getValue());
        }
    }

    /**
     * 把配置项写到本地文件，以便后面恢复的时候使用
     */
    private void writeConfigsToLocal(List<ConfigItem> configs) {
        String content = jsonConvert.serializeObject(configs);
        String fileName = GenerateCacheFileName();
        String cacheDir = this.options.getCacheDirectory();
        if (!cacheDir.isEmpty()) {
            File folder = new File(cacheDir);
            if (!folder.exists() && !folder.isDirectory()) {
                folder.mkdirs();
            }
        }
        Path path = Paths.get(cacheDir, fileName);
        try {
            Files.write(path, content.getBytes());
            logger.info("save config items cache to local file " + path + " successful .");
        }
        catch (Exception e) {
            logger.error("save config items cache to local file error .", e);
        }
    }

    /**
     * 从本地缓存的文件恢复配置项
     */
    private void reloadConfigsFromLocal() {
        String fileName = GenerateCacheFileName();
        Path path = Paths.get(this.options.getCacheDirectory(), fileName);

        try {
            if (Files.exists(path)) {
                byte[] data = Files.readAllBytes(path);
                String content = new String(data);
                if (content != null && !content.isEmpty()) {
                    List<ConfigItem> configs = jsonConvert.deserializeObject(content, new TypeReference<List<ConfigItem>>() {
                    });

                    if (configs != null) {
                        this.load(configs);

                        logger.info("load config items from local cache file " + path + " successful .");
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error("load config items from local cache file error .", e);
        }
    }

    private String GenerateCacheFileName () {
       // private string LocalCacheFileName => Path.Combine(_options.CacheDirectory, $"{_options.AppId}.agileconfig.client.configs.cache");
        String fileName = String.format("%s.agileconfig.client.configs.cache", this.options.getAppId());

        return  fileName;
    }

    /**
     * 从远程服务器拉取配置项
     *
     * @param nodeAddress 远程服务器地址
     */
    private List<ConfigItem> getConfigsFromRemote(String nodeAddress) {
        return configLoader.getConfigs(nodeAddress, options.getAppId(), options.getSecret(), options.getEnv());
    }
}
