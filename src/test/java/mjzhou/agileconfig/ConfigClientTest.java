package mjzhou.agileconfig;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigClientTest {

    @Test
    void load() {
        String node = "http://agileconfig_server.xbaby.xyz";
        String appId = "test_app";
        String appSecret = "test_app";
        String env = "";
        Options op = new Options(node,appId,appSecret,env);
        ConfigClient client =new ConfigClient(op);
        client.load();
        assertNotNull(client.getConfigs());
        Map<String,String> configs = client.getConfigs();
        for (Map.Entry<String,String> kv: configs.entrySet()
             ) {
            System.out.println(kv.getKey() + "=" + kv.getValue());
        }
    }
}