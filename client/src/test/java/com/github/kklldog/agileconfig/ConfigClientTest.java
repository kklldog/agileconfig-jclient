package com.github.kklldog.agileconfig;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigClientTest {

    @Test
    void load() {
        // 测试不从本地恢复
        String node = "http://agileconfig_server.xbaby.xyz";
        String appId = "test_app";
        String appSecret = "x";
        String env = "DEV";
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
        assertNotNull(client2.getConfigs());
        // test cacheDir
        appSecret = "test_app";
        Options op3 = new Options(node, appId, appSecret, env);
        op3.setCacheDirectory("c:/logs");
        ConfigClient client3 = new ConfigClient(op3);
        client3.load();
        assertNotNull(client3.getConfigs());
        // test not exist dir
        appSecret = "test_app";
        Options op4 = new Options(node, appId, appSecret, env);
        op4.setCacheDirectory("c:/logs/" + LocalDateTime.now().hashCode());
        ConfigClient client4 = new ConfigClient(op4);
        client4.load();
        assertNotNull(client4.getConfigs());
    }

    @Test
    void connect() {
        String node = "http://agileconfig-server.xbaby.xyz";
        String appId = "test_app";
        String appSecret = "test_app";
        String env = "DEV";
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
        String env = "DEV";
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

    @Test
    void testLoad() {
        List<ConfigItem> configs = new ArrayList<>();
        configs.add(new ConfigItem("1","2","3"));
        configs.add(new ConfigItem("test","dt","fsdfsdf"));
        configs.add(new ConfigItem("","dbstr","192.68.0.1"));

        String node = "http://agileconfig-server.xbaby.xyz";
        String appId = "test_app";
        String appSecret = "test_app";
        String env = "DEV";
        Options op = new Options(node, appId, appSecret, env);
        ConfigClient client = new ConfigClient(op);

        client.load(configs);

        String val = client.get("x");
        assertEquals("", val);
        String val1 = client.get("1:2");
        assertEquals("3", val1);
        String val2 = client.get("test:dt");
        assertEquals("fsdfsdf", val2);
        String val3 = client.get("dbstr");
        assertEquals("192.68.0.1", val3);
    }

    @Test
    void md5Version() {
        List<ConfigItem> configs = new ArrayList<>();
        configs.add(new ConfigItem("1","2","3"));
        configs.add(new ConfigItem("test","dt","fsdfsdf"));
        configs.add(new ConfigItem("","dbstr","192.68.0.1"));

        String node = "http://agileconfig-server.xbaby.xyz";
        String appId = "test_app";
        String appSecret = "test_app";
        String env = "DEV";
        Options op = new Options(node, appId, appSecret, env);
        ConfigClient client = new ConfigClient(op);

        client.load(configs);

        String md5 = client.md5Version();
        //String exp = "1:2&dbstr&test:dt&192.68.0.1&3&fsdfsdf";
        String exp = "8FA6A5F1EE70BCE760765A6EDFFC80AB" ;
        assertEquals(exp, md5);

    }
}