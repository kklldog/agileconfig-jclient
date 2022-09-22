package mjzhou.agileconfig;

public class ConfigItem {

    public ConfigItem(){
    }

    public ConfigItem(String group, String key, String val) {
        this.group = group;
        this.key = key;
        this.value = val;
    }

    private String key;
    private String value;
    private String group;

    public String getKey() {
        if (key == null)
            return "";
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getValue() {
        if (value == null)
            return "";
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public String getGroup() {
        if (group == null)
            return "";
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
