package com.github.kklldog.agileconfig;

import java.util.Date;
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
     * get config items by group name
     */
    List<ConfigItem> getGroup(String groupName);

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

    /**
     * md5 of the current config data
     */
    String md5Version();

    /**
     * 与服务端比较的版本号: 优先用服务端下发的 publishTimeLineId , 没有则用 md5
     */
    String localVersion();

    /**
     * 当前连接状态
     */
    ConnectStatus getStatus();

    /**
     * 当前配置是否来自本地缓存文件
     */
    boolean isLoadFromLocal();

    /**
     * 最后一次从服务端成功拉取配置的时间, 未成功拉取过则为 null
     */
    Date getLastLoadedTimeFromServer();

    /**
     * 注册配置重载监听器
     */
    void addReloadedListener(ConfigReloadedListener listener);

    /**
     * 移除配置重载监听器
     */
    void removeReloadedListener(ConfigReloadedListener listener);
}
