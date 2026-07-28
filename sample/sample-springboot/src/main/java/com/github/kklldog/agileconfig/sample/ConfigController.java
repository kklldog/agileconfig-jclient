package com.github.kklldog.agileconfig.sample;

import com.github.kklldog.agileconfig.IConfigClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 演示在业务代码中注入 IConfigClient 读取配置。
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    private final IConfigClient configClient;

    public ConfigController(IConfigClient configClient) {
        this.configClient = configClient;
    }

    /**
     * 获取全部配置
     */
    @GetMapping
    public Map<String, String> all() {
        return configClient.getConfigs();
    }

    /**
     * 按 key 获取单个配置，例如 GET /config/db:connection
     */
    @GetMapping("/{key}")
    public String get(@PathVariable String key) {
        String value = configClient.get(key);
        return value == null ? "(not found)" : value;
    }
}
