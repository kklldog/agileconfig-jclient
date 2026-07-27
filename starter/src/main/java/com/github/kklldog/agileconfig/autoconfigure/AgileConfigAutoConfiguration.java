package com.github.kklldog.agileconfig.autoconfigure;

import com.github.kklldog.agileconfig.ConfigClient;
import com.github.kklldog.agileconfig.IConfigClient;
import com.github.kklldog.agileconfig.Options;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author gao shiyong
 * @since 2022/10/8 16:48
 */
@Configuration
@ComponentScan(value = "com.github.kklldog.agileconfig.**")
public class AgileConfigAutoConfiguration {

    @Value("${agile.config.node}")
    private String node;
    @Value("${agile.config.appId:}")
    private String appId;
    @Value("${agile.config.secret:}")
    private String secret;
    @Value("${agile.config.env:dev}")
    private String env;
    @Value("${agile.config.name:}")
    private String name;
    @Value("${agile.config.tag:}")
    private String tag;
    @Value("${agile.config.httpTimeout:100}")
    private int httpTimeout;
    @Value("${agile.config.reconnectInterval:5}")
    private int reconnectInterval;
    @Value("${agile.config.cache.enabled:true}")
    private boolean cacheEnabled;
    @Value("${agile.config.cache.directory:}")
    private String cacheDirectory;
    @Value("${agile.config.cache.configEncrypt:false}")
    private boolean configCacheEncrypt;

    @Bean
    @ConditionalOnMissingBean(name = "agileConfigClient")
    public IConfigClient configClient() {
        Options options = new Options(node, appId, secret, env);
        options.setName(name);
        options.setTag(tag);
        options.setHttpTimeout(httpTimeout);
        options.setReconnectInterval(reconnectInterval);
        options.setCacheEnabled(cacheEnabled);
        options.setCacheDirectory(cacheDirectory);
        options.setConfigCacheEncrypt(configCacheEncrypt);

        return new ConfigClient(options);
    }
}
