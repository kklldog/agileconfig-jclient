# agileconfig-jclient

A java client for [AgileConfig](https://github.com/dotnetcore/AgileConfig).

AgileConfig 的 Java 客户端。通过 HTTP 拉取配置，通过 WebSocket 接收服务端推送的变更通知，支持本地缓存、断线重连、多节点容灾。

## 特性

- 从 AgileConfig 服务端拉取配置，支持多节点（随机选择 + 失败切换）
- WebSocket 长连接，服务端配置发布后自动重新加载
- 心跳保活（30 秒）与断线自动重连
- 配置本地文件缓存，所有节点不可用时可从本地缓存恢复
- 本地缓存文件可选 AES 加密
- 配置重载事件监听
- Spring Boot Starter 开箱即用

## 环境要求

- JDK 8+
- Maven 3.x
- AgileConfig 服务端

## 模块说明

| 模块 | artifactId | 说明 |
| --- | --- | --- |
| client | `agileconfig-client` | 核心客户端，无 Spring 依赖，可独立使用 |
| starter | `agileconfig-boot-starter` | Spring Boot 自动装配 |

## 安装

当前版本为 `1.0-SNAPSHOT`，请先在项目根目录安装到本地仓库：

```bash
mvn clean install -DskipTests
```

原生 Java 项目只需引用 client：

```xml
<dependency>
    <groupId>com.github.kklldog.agileconfig</groupId>
    <artifactId>agileconfig-client</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Spring Boot 项目引用 starter：

```xml
<dependency>
    <groupId>com.github.kklldog.agileconfig</groupId>
    <artifactId>agileconfig-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## 快速开始

### 1. 原生 Java 使用

```java
import com.github.kklldog.agileconfig.ConfigClient;
import com.github.kklldog.agileconfig.IConfigClient;
import com.github.kklldog.agileconfig.Options;

public class Demo {
    public static void main(String[] args) {
        // nodes 支持多个节点，用英文逗号分隔
        Options options = new Options(
                "http://localhost:5000,http://localhost:5001",
                "app_id",
                "app_secret",
                "DEV");
        options.setName("my-java-app");
        options.setTag("order");

        IConfigClient client = new ConfigClient(options);

        // 连接服务端并拉取配置，内部会启动心跳与重连线程
        client.connect();

        String value = client.get("db:connection");
        System.out.println(value);

        // 应用退出时断开
        Runtime.getRuntime().addShutdownHook(new Thread(client::disconnect));
    }
}
```

### 2. Spring Boot 使用

在 `application.yml` 中配置：

```yaml
agile:
  config:
    node: http://localhost:5000,http://localhost:5001
    appId: app_id
    secret: app_secret
    env: DEV
    name: my-spring-app
    tag: order
    httpTimeout: 100
    reconnectInterval: 5
    cache:
      enabled: true
      directory: ./config-cache
      configEncrypt: false
```

starter 会自动注册一个 `IConfigClient` Bean。注意：**Bean 创建后不会自动连接**，需要自行触发 `connect()`，例如：

```java
import com.github.kklldog.agileconfig.IConfigClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgileConfigInitializer {

    @Bean
    public ApplicationRunner agileConfigRunner(IConfigClient configClient) {
        return args -> configClient.connect();
    }
}
```

然后在任意 Bean 中注入使用：

```java
@Service
public class OrderService {

    private final IConfigClient configClient;

    public OrderService(IConfigClient configClient) {
        this.configClient = configClient;
    }

    public String dbConnection() {
        return configClient.get("db:connection");
    }
}
```

如果需要自定义客户端，只要自己声明一个名为 `agileConfigClient` 的 Bean，自动装配就会退让（`@ConditionalOnMissingBean(name = "agileConfigClient")`）。

## 读取配置

配置项的 key 规则：

- 无分组：直接使用 `key`
- 有分组：`group:key`

key 的比较**忽略大小写**。

```java
// 单个配置，不存在时返回空字符串 ""
String val = client.get("db:connection");

// 全部配置
Map<String, String> all = client.getConfigs();

// 按分组取
List<ConfigItem> dbItems = client.getGroup("db");
```

## 监听配置变更

服务端发布配置后，客户端会自动重新拉取并触发监听器：

```java
client.addReloadedListener(args -> {
    Map<String, String> oldConfigs = args.getOldConfigs();
    Map<String, String> newConfigs = args.getNewConfigs();
    newConfigs.forEach((k, v) -> {
        if (!v.equals(oldConfigs.get(k))) {
            System.out.println("changed: " + k + " = " + v);
        }
    });
});

// 不再需要时移除
// client.removeReloadedListener(listener);
```

## 配置项（Options）

| 属性 | Spring 配置项 | 默认值 | 说明 |
| --- | --- | --- | --- |
| nodes | `agile.config.node` | 无（必填） | 服务端地址，多个用逗号分隔 |
| appId | `agile.config.appId` | `""` | 应用 ID |
| secret | `agile.config.secret` | `""` | 应用密钥 |
| env | `agile.config.env` | `dev` | 环境标识，内部会转成大写 |
| name | `agile.config.name` | `""` | 客户端名称，上报给服务端展示 |
| tag | `agile.config.tag` | `""` | 客户端标签，上报给服务端展示 |
| httpTimeout | `agile.config.httpTimeout` | `100` | HTTP 请求超时时间（秒） |
| reconnectInterval | `agile.config.reconnectInterval` | `5` | 断线重连间隔（秒） |
| cacheEnabled | `agile.config.cache.enabled` | `true` | 是否把配置缓存到本地文件 |
| cacheDirectory | `agile.config.cache.directory` | `""`（当前工作目录） | 本地缓存目录 |
| configCacheEncrypt | `agile.config.cache.configEncrypt` | `false` | 是否用 secret 对缓存文件做 AES 加密 |
| reloadFromLocal | 无 | `true` | 所有节点都拉取失败时是否从本地缓存恢复 |

本地缓存文件名为 `{appId}.agileconfig.client.configs.cache`。

## 客户端状态

```java
ConnectStatus status = client.getStatus();      // Disconnected / Connecting / Connected
boolean fromLocal = client.isLoadFromLocal();   // 当前配置是否来自本地缓存
Date lastLoaded = client.getLastLoadedTimeFromServer(); // 最后一次成功从服务端拉取的时间
String version = client.localVersion();         // 与服务端比较的版本号
String md5 = client.md5Version();               // 当前配置数据的 md5
```

## 构建与测试

```bash
# 编译并安装
mvn clean install

# 只跑测试
mvn test
```

## License

见 [LICENSE.md](LICENSE.md)。
