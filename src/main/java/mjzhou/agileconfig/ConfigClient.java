package mjzhou.agileconfig;

import java.util.List;
import java.util.Map;

public class ConfigClient implements IConfigClient {

    private Options options;
    private Map<String, String> data;

    public ConfigClient(Options options) {
        this.options = options;
    }

    @Override
    public Map<String, String> getConfigs() {
        return null;
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public void connect() {

    }

    @Override
    public void load() {

    }
}
