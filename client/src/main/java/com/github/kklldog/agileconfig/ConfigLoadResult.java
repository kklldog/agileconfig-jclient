package com.github.kklldog.agileconfig;

import java.util.List;

/**
 * 一次配置拉取的结果
 */
public class ConfigLoadResult {
    private final List<ConfigItem> configs;
    private final String content;
    private final String publishTimeLineId;

    public ConfigLoadResult(List<ConfigItem> configs, String content, String publishTimeLineId) {
        this.configs = configs;
        this.content = content;
        this.publishTimeLineId = publishTimeLineId;
    }

    public List<ConfigItem> getConfigs() {
        return configs;
    }

    /**
     * 服务端返回的原始 json , 原样缓存到本地文件
     */
    public String getContent() {
        return content;
    }

    /**
     * 服务端返回的 publish-time-line-id 响应头, 可能为空
     */
    public String getPublishTimeLineId() {
        return publishTimeLineId == null ? "" : publishTimeLineId;
    }
}
