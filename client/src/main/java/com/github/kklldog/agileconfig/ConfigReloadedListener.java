package com.github.kklldog.agileconfig;

/**
 * 最新的配置加载到本地后触发
 */
public interface ConfigReloadedListener {
    void onReloaded(ConfigReloadedArgs args);
}
