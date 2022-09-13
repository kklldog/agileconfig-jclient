package mjzhou.agileconfig;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultHttpConfigLoaderTest {

    @Test
    void getConfigs() {
        String node = "http://agileconfig_server.xbaby.xyz";
        String appId = "test_app";
        String appSecret = "test_app";

        IConfigLoader loader = new DefaultHttpConfigLoader();
        List<ConfigItem> resp = loader.getConfigs(node, appId, appSecret);

        assertNotNull(resp);
    }
}