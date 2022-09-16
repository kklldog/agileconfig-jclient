package mjzhou.agileconfig;

public class Options {
    private final String strNodes;
    private final String[] nodeArray;
    private final String appId;
    private final String secret;

    public String getEnv() {
        return env;
    }

    private final String env;

    public Options(String nodes, String appId, String secret, String env) {
        this.strNodes = ensureEmptyString(nodes);
        this.appId = ensureEmptyString(appId);
        this.secret = ensureEmptyString(secret);
        this.env = ensureEmptyString(env);

        nodeArray = strNodes.split(",");
    }

    private String ensureEmptyString(String str){
        if (str == null) {
            return "";
        }

        return str;
    }

    public String getAppId() {
        return appId;
    }

    public String[] getNodes() {
        return nodeArray;
    }

    public String getSecret() {
        return secret;
    }

}
