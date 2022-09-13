package mjzhou.agileconfig;

import java.util.List;

public interface IConfigLoader {
    public List<ConfigItem> getConfigs(String node, String appId, String appSecret);
}
