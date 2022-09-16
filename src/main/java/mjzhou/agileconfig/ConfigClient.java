package mjzhou.agileconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.krb5.Config;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ConfigClient implements IConfigClient {
    private static final Logger logger =  LoggerFactory.getLogger(ConfigClient.class);
    private final Options options;
    private Map<String, String> data;
    private final IConfigLoader configLoader;

    public ConfigClient(Options options) {
        configLoader = new DefaultHttpConfigLoader();
        this.options = options;
    }

    @Override
    public Map<String, String> getConfigs() {
        return data;
    }

    @Override
    public String get(String key) {
        if (data == null) {
            return "";
        }

        if (data.containsKey(key)) {
            return data.get(key);
        }

        return  "";
    }

    @Override
    public void connect() {

    }

    @Override
    public void load() {
        String[] nodes = options.getNodes();
        boolean testAll = false;
        for (int i = 0; i < nodes.length; i++) {
            String node = nodes[i];
            List<ConfigItem> configs = getConfigsFromRemote(node);
            if (configs == null) {
                if (i == nodes.length -1) {
                    logger.info(String.format("can NOT get app's configs from all nodes . appid: %s", options.getAppId()));
                    testAll = true;
                }
                continue; // cant get configs from node , so try next .
            }

            logger.info(String.format("get app's configs from node success . appid: %s , node: %s", options.getAppId(), node));
            writeConfigsToLocal(configs); // 获取到配置后第一时间写到本地文件

            if (data == null) {
                data = new IdentityHashMap<>();
            }
            else {
                data.clear();
            }

            for (ConfigItem item: configs) {
                String key = "";
                if (item.getGroup().isEmpty()) {
                    key = item.getKey();
                }
                else {
                    key = item.getGroup() + ":" + item.getKey();
                }
                data.put(key, item.getValue());
            }

            break;
        }

        if (testAll) {
            // 如果所有的节点尝试获取配置都失败，那么从本地文件恢复
            reloadConfigsFromLocal();
        }
    }

    /**
     * 把配置项写到本地文件，以便后面恢复的时候使用
     */
    private void writeConfigsToLocal(List<ConfigItem> configs){

    }

    /**
     * 从本地缓存的文件恢复配置项
     */
    private void reloadConfigsFromLocal(){

    }

    /**
     * 从远程服务器拉取配置项
     * @param nodeAddress 远程服务器地址
     * @return
     */
    private List<ConfigItem> getConfigsFromRemote(String nodeAddress) {
        return configLoader.getConfigs(nodeAddress, options.getAppId(), options.getSecret(), options.getEnv());
    }
}
