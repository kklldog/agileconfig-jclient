package mjzhou.agileconfig;

import java.util.Map;

public interface IConfigClient {

    /**
     * get all config items
     * @return
     */
    Map<String,String> getConfigs();

    /**
     * get config value by key
     */
    String get(String key);

    /**
     *  connect to agileconfig server
     */
    void connect();

    /**
     * load config items from remote server
     */
    void load();
}
