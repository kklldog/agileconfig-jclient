package mjzhou.agileconfig;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConfigClientTest {

    @Test
    void load() {
        // 测试不从本地恢复
        String node = "http://agileconfig_server.xbaby.xyz";
        String appId = "test_app";
        String appSecret = "x";
        String env = "";
        Options op = new Options(node, appId, appSecret, env);
        op.setReloadFromLocal(false);
        ConfigClient client = new ConfigClient(op);
        client.load();
        assertNull(client.getConfigs());
        // 测试正常读取
        appSecret = "test_app";
        Options op1 = new Options(node, appId, appSecret, env);
        ConfigClient client1 = new ConfigClient(op1);
        client1.load();
        assertNotNull(client1.getConfigs());
        Map<String, String> configs1 = client1.getConfigs();
        for (Map.Entry<String, String> kv : configs1.entrySet()
        ) {
            System.out.println(kv.getKey() + "=" + kv.getValue());
        }
        //上一步写缓存后，测试从本地读取
        appSecret = "x";
        Options op2 = new Options(node, appId, appSecret, env);
        ConfigClient client2 = new ConfigClient(op2);
        client2.load();
        //assertNotNull(client2.getConfigs());
        //todo finish this ut
    }

    @Test
    void connect() {
        String node = "http://agileconfig-server.xbaby.xyz";
        String appId = "test_app";
        String appSecret = "test_app";
        String env = "";
        Options op = new Options(node, appId, appSecret, env);
        ConfigClient client = new ConfigClient(op);
        client.connect();
        assertNotNull(client.getConfigs());
        Map<String, String> configs = client.getConfigs();
        for (Map.Entry<String, String> kv : configs.entrySet()
        ) {
            System.out.println(kv.getKey() + "=" + kv.getValue());
            String val = client.get(kv.getKey());
            System.out.println(kv.getKey() + "=" + val);
        }

//        Scanner input = new Scanner(System.in);
//        input.next();
    }

    @Test
    void disconnect() {
        String node = "http://agileconfig-server.xbaby.xyz";
        String appId = "test_app";
        String appSecret = "test_app";
        String env = "";
        Options op = new Options(node, appId, appSecret, env);
        ConfigClient client = new ConfigClient(op);
        client.connect();
        assertNotNull(client.getConfigs());
        Map<String, String> configs = client.getConfigs();
        for (Map.Entry<String, String> kv : configs.entrySet()
        ) {
            System.out.println(kv.getKey() + "=" + kv.getValue());
        }

        client.disconnect();

        ConfigClient client1 = new ConfigClient(op);
        client1.disconnect();
    }
}