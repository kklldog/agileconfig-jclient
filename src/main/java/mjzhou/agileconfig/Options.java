package mjzhou.agileconfig;

public class Options {
    private final String strNodes;
    private final String[] nodeArray;
    private final String appId;
    private final String secret;

    public Options(String nodes, String appId, String secret) {
        this.strNodes = ensureEmptyString(nodes);
        this.appId = ensureEmptyString(appId);
        this.secret = ensureEmptyString(secret);

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
