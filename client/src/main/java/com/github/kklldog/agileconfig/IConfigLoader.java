package com.github.kklldog.agileconfig;

import java.util.List;

public interface IConfigLoader {
    List<ConfigItem> getConfigs(String node, String appId, String appSecret, String env);
}
