# Sample

演示如何使用 agileconfig-jclient。

| 模块 | 说明 |
| --- | --- |
| sample-java | 原生 Java 项目直接使用 `client` |
| sample-springboot | Spring Boot 项目使用 `agileconfig-boot-starter` |

## 准备

1. 启动 AgileConfig 服务端，创建应用并添加若干配置项（例如 `db:connection`）。
2. 修改示例中的节点地址、appId、secret、env：
   - sample-java：[JavaSample.java](sample-java/src/main/java/com/github/kklldog/agileconfig/sample/JavaSample.java)
   - sample-springboot：[application.yml](sample-springboot/src/main/resources/application.yml)
3. 在仓库根目录安装依赖到本地仓库：

```bash
mvn clean install -DskipTests
```

## 运行 sample-java

```bash
cd sample/sample-java
mvn compile exec:java
```

程序会打印配置内容并保持运行，在服务端发布配置变更后可观察到自动重载。

## 运行 sample-springboot

```bash
cd sample/sample-springboot
mvn spring-boot:run
```

启动后访问：

- `http://localhost:8080/config` — 全部配置
- `http://localhost:8080/config/db:connection` — 按 key 读取
