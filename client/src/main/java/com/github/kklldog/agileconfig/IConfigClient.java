package com.github.kklldog.agileconfig;

import java.util.List;
import java.util.Map;

public interface IConfigClient {

    /**
     * get all config items
     *
     * @return
     */
    Map<String, String> getConfigs();

    /**
     * get config value by key
     */
    String get(String key);

    /**
     * connect to agileconfig server
     */
    void connect();

    /**
     * load config items from remote server
     */
    void load();

    /**
     * load config items from source
     * @param configs
     */
    void load(List<ConfigItem> configs);

    /**
     * disconnect from server
     */
    void disconnect();

    String md5Version();
}
