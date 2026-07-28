package com.github.kklldog.agileconfig.sample;

import com.github.kklldog.agileconfig.IConfigClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot 使用 agileconfig-boot-starter 的示例。
 * <p>
 * starter 会根据 application.yml 中 agile.config.* 的配置自动注册 {@link IConfigClient} Bean，
 * 但不会自动连接，需要自行触发 connect()，这里通过 ApplicationRunner 在启动完成后连接。
 * <p>
 * 运行方式：
 * <pre>
 * cd sample/sample-springboot
 * mvn spring-boot:run
 * </pre>
 * 然后访问 http://localhost:8080/config 或 http://localhost:8080/config/db:connection
 */
@SpringBootApplication
public class SpringBootSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSampleApplication.class, args);
    }

    @Bean
    public ApplicationRunner agileConfigRunner(IConfigClient configClient) {
        return args -> {
            // 注册配置重载监听
            configClient.addReloadedListener(evt ->
                    System.out.println("config reloaded, new configs: " + evt.getNewConfigs()));
            // 连接服务端并拉取配置
            configClient.connect();
        };
    }
}
