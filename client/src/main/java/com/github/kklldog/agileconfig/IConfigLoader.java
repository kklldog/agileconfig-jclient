package com.github.kklldog.agileconfig;

import java.util.List;

public interface IConfigLoader {
    /**
     * 拉取配置, 使用默认超时时间
     */
    List<ConfigItem> getConfigs(String node, String appId, String appSecret, String env);

    /**
     * 拉取配置, 同时返回原始报文与 publish-time-line-id
     *
     * @param timeoutSeconds http 超时时间, 单位秒
     */
    ConfigLoadResult load(String node, String appId, String appSecret, String env, int timeoutSeconds);
}
