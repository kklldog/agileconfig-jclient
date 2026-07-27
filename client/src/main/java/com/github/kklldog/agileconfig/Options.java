package com.github.kklldog.agileconfig;

public class Options {
    private final String strNodes;
    private final String[] nodeArray;
    private final String appId;
    private final String secret;
    private final String env;
    private String name;
    private String tag;
    private String cacheDirectory;
    private boolean reloadFromLocal = true;
    private boolean cacheEnabled = true;
    private boolean configCacheEncrypt = false;
    private int httpTimeout = 100;
    private int reconnectInterval = 5;

    public boolean isReloadFromLocal() {
        return reloadFromLocal;
    }

    /**
     * 如果所有的节点都连不上的时候直接从本地恢复, 默认 true
     * @param reloadFromLocal
     */
    public void setReloadFromLocal(boolean reloadFromLocal) {
        this.reloadFromLocal = reloadFromLocal;
    }

    /**
     * 是否把配置缓存到本地文件, 默认 true
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    /**
     * 是否加密本地缓存文件, 默认 false
     */
    public boolean isConfigCacheEncrypt() {
        return configCacheEncrypt;
    }

    public void setConfigCacheEncrypt(boolean configCacheEncrypt) {
        this.configCacheEncrypt = configCacheEncrypt;
    }

    /**
     * http 请求超时时间, 单位秒, 默认 100
     */
    public int getHttpTimeout() {
        return httpTimeout;
    }

    public void setHttpTimeout(int httpTimeout) {
        this.httpTimeout = httpTimeout;
    }

    /**
     * 断线重连的间隔, 单位秒, 默认 5
     */
    public int getReconnectInterval() {
        return reconnectInterval;
    }

    public void setReconnectInterval(int reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    /**
     * 客户端名称, 上报给服务端用于展示
     */
    public String getName() {
        return ensureEmptyString(this.name);
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 客户端标签, 上报给服务端用于展示
     */
    public String getTag() {
        return ensureEmptyString(this.tag);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCacheDirectory() {
        return ensureEmptyString(this.cacheDirectory);
    }

    public void setCacheDirectory(String cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    public String getEnv() {
        return env;
    }

    public Options(String nodes, String appId, String secret, String env) {
        this.strNodes = ensureEmptyString(nodes);
        this.appId = ensureEmptyString(appId);
        this.secret = ensureEmptyString(secret);
        this.env = ensureEmptyString(env).toUpperCase();

        nodeArray = strNodes.split(",");
    }

    private String ensureEmptyString(String str) {
        if (str == null) {
            return "";
        }

        return str;
    }

    public String getAppId() {
        return appId;
    }

    public String[] getNodes() {
        return nodeArray;
    }

    public String getSecret() {
        return secret;
    }

}
