package com.github.kklldog.agileconfig.sample;

import com.github.kklldog.agileconfig.ConfigClient;
import com.github.kklldog.agileconfig.ConfigItem;
import com.github.kklldog.agileconfig.IConfigClient;
import com.github.kklldog.agileconfig.Options;

import java.util.List;
import java.util.Map;

/**
 * 原生 Java 使用 agileconfig-jclient 的示例。
 * <p>
 * 运行前请先启动 AgileConfig 服务端，并把下面的节点地址、appId、secret、env 改成自己的。
 * <p>
 * 运行方式：
 * <pre>
 * cd sample/sample-java
 * mvn compile exec:java
 * </pre>
 */
public class JavaSample {

    public static void main(String[] args) throws Exception {
        // nodes 支持多个节点，用英文逗号分隔
        Options options = new Options(
                "http://localhost:5000",
                "test_app",
                "test_secret",
                "DEV");
        // 客户端名称，方便在服务端界面上识别
        options.setName("sample-java");
        // 可选：按 tag 过滤
        // options.setTag("order");
        // 本地缓存，所有节点不可用时可从缓存恢复配置
        options.setCacheEnabled(true);
        options.setCacheDirectory("./config-cache");

        IConfigClient client = new ConfigClient(options);

        // 注册配置重载监听：服务端发布配置后客户端会自动重新加载并触发该事件
        client.addReloadedListener(evt -> {
            System.out.println("===== config reloaded =====");
            System.out.println("old: " + evt.getOldConfigs());
            System.out.println("new: " + evt.getNewConfigs());
        });

        // 连接服务端并拉取配置，内部会启动 WebSocket 心跳与断线重连
        client.connect();

        // 1. 按 key 读取单个配置
        String value = client.get("db:connection");
        System.out.println("db:connection = " + value);

        // 2. 读取全部配置
        Map<String, String> all = client.getConfigs();
        System.out.println("all configs: " + all);

        // 3. 按 group 读取
        List<ConfigItem> group = client.getGroup("db");
        System.out.println("group [db]: " + group);

        // 4. 当前状态与版本
        System.out.println("status = " + client.getStatus());
        System.out.println("md5 = " + client.md5Version());

        // 应用退出时断开连接
        Runtime.getRuntime().addShutdownHook(new Thread(client::disconnect));

        // 保持进程运行，方便在服务端修改配置后观察自动重载效果
        System.out.println("running... publish a config change on server to see auto reload. Ctrl+C to exit.");
        Thread.currentThread().join();
    }
}
