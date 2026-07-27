package com.github.kklldog.agileconfig;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.kklldog.agileconfig.websocket.WebsocketClientEndpoint;
import com.github.kklldog.agileconfig.websocket.WebsocketMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.github.kklldog.agileconfig.Encrypt.md5;

public class ConfigClient implements IConfigClient {
    private static final Logger logger = LoggerFactory.getLogger(ConfigClient.class);

    private final IJsonConvert jsonConvert = new DefaultJsonConvert();

    private final Options options;
    private Map<String, String> data;
    private List<ConfigItem> configs = new ArrayList<>();
    private final IConfigLoader configLoader;

    private Thread pingThread;
    private Thread reconnectThread;
    private static final int pingInterval = 30;
    private WebsocketClientEndpoint websocketClient;

    private volatile ConnectStatus status = ConnectStatus.Disconnected;
    private volatile boolean loadFromLocal = false;
    private volatile Date lastLoadedTimeFromServer;
    private volatile String currentPublishTimeLineId = "";

    private final List<ConfigReloadedListener> reloadedListeners = new CopyOnWriteArrayList<>();

    public ConfigClient(Options options) {
        configLoader = new DefaultHttpConfigLoader();
        this.options = options;
    }

    public Options getOptions() {
        return options;
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

    @Override
    public List<ConfigItem> getGroup(String groupName) {
        if (configs == null) {
            return null;
        }

        return configs.stream()
                .filter(x -> x.getGroup().equals(groupName == null ? "" : groupName))
                .collect(Collectors.toList());
    }

    @Override
    public ConnectStatus getStatus() {
        return status;
    }

    @Override
    public boolean isLoadFromLocal() {
        return loadFromLocal;
    }

    @Override
    public Date getLastLoadedTimeFromServer() {
        return lastLoadedTimeFromServer;
    }

    @Override
    public void addReloadedListener(ConfigReloadedListener listener) {
        if (listener != null) {
            reloadedListeners.add(listener);
        }
    }

    @Override
    public void removeReloadedListener(ConfigReloadedListener listener) {
        reloadedListeners.remove(listener);
    }

    private boolean tryConnect() {
        this.status = ConnectStatus.Connecting;
        RandomServers randomServer = new RandomServers(this.options.getNodes());
        while (!randomServer.isComplete()) {
            String node = randomServer.next();
            WebsocketClientEndpoint endpoint = new WebsocketClientEndpoint(this.options.getAppId(), this.options.getSecret(),
                    this.options.getEnv(), node, this.options.getName(), this.options.getTag());
            endpoint.addMessageHandler(new WebsocketMessageHandler(this));
            try {
                endpoint.connect();
                this.websocketClient = endpoint;
                this.status = ConnectStatus.Connected;
                logger.info("connect to server " + endpoint.getNodeAddress() + " successful .");
                return true;
            } catch (Exception e) {
                logger.error(String.format("try connect to server %s fail .", node), e);
            }
        }

        logger.info("all the server can not be connected .");
        this.status = ConnectStatus.Disconnected;

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
        if (pingThread != null) {
            pingThread.interrupt();
            pingThread = null;
        }
        if (reconnectThread != null) {
            reconnectThread.interrupt();
            reconnectThread = null;
        }
        if (websocketClient != null) {
            websocketClient.disconnect();
            websocketClient = null;
        }
        this.status = ConnectStatus.Disconnected;
    }

    @Override
    public String md5Version() {
        if (data == null) {
            return  "";
        }

        // 与 C# 客户端一致: 使用 Ordinal 排序
        List<String> keys =  this.data.keySet().stream().sorted().collect(Collectors.toList());
        List<String> vals = this.data.values().stream().sorted().collect(Collectors.toList());
        String strKeys = String.join("&", keys);
        String strVals = String.join("&", vals);
        String sourceText = strKeys + "&" + strVals;

        return  md5(sourceText).toUpperCase();
    }

    @Override
    public String localVersion() {
        if (currentPublishTimeLineId == null || currentPublishTimeLineId.isEmpty()) {
            return md5Version();
        }

        return currentPublishTimeLineId;
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
                        Thread.currentThread().interrupt();
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
                    Thread.sleep(this.options.getReconnectInterval() * 1000L);
                    if (websocketClient != null && websocketClient.isOpened()) {
                        continue;
                    }

                    if (tryConnect()) {
                        load();
                    }
                } catch (InterruptedException e) {
                    logger.info("AutoReconnect Thread Interrupted .", e);
                    Thread.currentThread().interrupt();
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
            ConfigLoadResult result = configLoader.load(node, options.getAppId(), options.getSecret(),
                    options.getEnv(), options.getHttpTimeout());
            if (result == null) {
                if (idx == nodes.length - 1) {
                    logger.info(String.format("can NOT get app's configs from all nodes . appid: %s", options.getAppId()));
                    testAll = true;
                    break;
                }
                idx++;
                continue; // cant get configs from node , so try next .
            }

            logger.info(String.format("get app's configs from node success . appid: %s , node: %s", options.getAppId(), node));

            this.currentPublishTimeLineId = result.getPublishTimeLineId();
            this.lastLoadedTimeFromServer = new Date();

            writeConfigsToLocal(result.getContent()); // 获取到配置后第一时间写到本地文件

            this.load(result.getConfigs());
            this.loadFromLocal = false;

            sendLoadedNoticeToServer();

            break;
        }

        if (testAll && this.options.isReloadFromLocal()) {
            // 如果所有的节点尝试获取配置都失败，那么从本地文件恢复
            reloadConfigsFromLocal();
        }
    }

    @Override
    public void load(List<ConfigItem> configs) {
        Map<String, String> oldData = copyConfigDict();

        this.configs = configs == null ? new ArrayList<>() : configs;

        // 与 C# 客户端一致: key 忽略大小写
        Map<String, String> tempData = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);
        for (ConfigItem item : this.configs) {
            String key;
            if (item.getGroup().isEmpty()) {
                key = item.getKey();
            } else {
                key = item.getGroup() + ":" + item.getKey();
            }
            tempData.putIfAbsent(key, item.getValue());
        }
        this.data = tempData;

        Map<String, String> newData = copyConfigDict();
        raiseReloaded(new ConfigReloadedArgs(oldData, newData));
    }

    private Map<String, String> copyConfigDict() {
        Map<String, String> dict = new LinkedHashMap<>();
        if (this.data != null) {
            dict.putAll(this.data);
        }

        return dict;
    }

    private void raiseReloaded(ConfigReloadedArgs args) {
        for (ConfigReloadedListener listener : reloadedListeners) {
            try {
                listener.onReloaded(args);
            } catch (Exception e) {
                logger.error("invoke reloaded listener error .", e);
            }
        }
    }

    /**
     * 配置加载完毕后通知服务端
     */
    private void sendLoadedNoticeToServer() {
        if (websocketClient != null && websocketClient.isOpened()) {
            try {
                websocketClient.sendMessage("loaded");
                logger.trace("client send 'loaded' to server by websocket .");
            } catch (Exception e) {
                logger.error("client try to send loaded msg to server but failed.", e);
            }
        }
    }

    /**
     * 把配置项写到本地文件，以便后面恢复的时候使用
     */
    private void writeConfigsToLocal(String content) {
        if (!this.options.isCacheEnabled()) {
            return;
        }
        if (content == null || content.isEmpty()) {
            return;
        }

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
            if (this.options.isConfigCacheEncrypt()) {
                content = Encrypt.aesEncryptECB(this.options.getSecret(), content);
            }
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
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
                String content = new String(data, StandardCharsets.UTF_8);
                if (!content.isEmpty()) {
                    if (this.options.isConfigCacheEncrypt()) {
                        content = Encrypt.aesDecryptECB(this.options.getSecret(), content);
                    }

                    List<ConfigItem> configs = jsonConvert.deserializeObject(content, new TypeReference<List<ConfigItem>>() {
                    });

                    if (configs != null) {
                        this.load(configs);
                        this.loadFromLocal = true;
                        this.currentPublishTimeLineId = "";

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
}
