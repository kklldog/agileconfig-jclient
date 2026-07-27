package com.github.kklldog.agileconfig;

import java.util.Collections;
import java.util.Map;

/**
 * 配置重新加载完毕后的事件参数
 */
public class ConfigReloadedArgs {
    private final Map<String, String> oldConfigs;
    private final Map<String, String> newConfigs;

    public ConfigReloadedArgs(Map<String, String> oldConfigs, Map<String, String> newConfigs) {
        this.oldConfigs = Collections.unmodifiableMap(oldConfigs);
        this.newConfigs = Collections.unmodifiableMap(newConfigs);
    }

    public Map<String, String> getOldConfigs() {
        return oldConfigs;
    }

    public Map<String, String> getNewConfigs() {
        return newConfigs;
    }
}
