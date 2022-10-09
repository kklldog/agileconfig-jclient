package com.github.kklldog.agileconfig;

public class Options {
    private final String strNodes;
    private final String[] nodeArray;
    private final String appId;
    private final String secret;
    private String cacheDirectory;
    private boolean reloadFromLocal = true;

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

    public String getCacheDirectory() {
        return ensureEmptyString(this.cacheDirectory);
    }

    public void setCacheDirectory(String cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    public String getEnv() {
        return env;
    }

    private final String env;

    public Options(String nodes, String appId, String secret, String env) {
        this.strNodes = ensureEmptyString(nodes);
        this.appId = ensureEmptyString(appId);
        this.secret = ensureEmptyString(secret);
        this.env = ensureEmptyString(env);

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
