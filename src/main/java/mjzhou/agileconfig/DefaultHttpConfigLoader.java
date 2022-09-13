package mjzhou.agileconfig;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import jdk.nashorn.internal.runtime.regexp.joni.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DefaultHttpConfigLoader implements IConfigLoader {

    private static final Logger logger =  LoggerFactory.getLogger(DefaultHttpConfigLoader.class);
    private static final IJsonConvert jsonConvert = new DefaultJsonConvert();

    @Override
    public List<ConfigItem> getConfigs(String node, String appId, String appSecret) {
        if (!node.endsWith("/")){
            node += "/";
        }
        String reqUrl = node + "api/config/app/" + appId;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //set basic auth header .
            String encoded = Base64.getEncoder().encodeToString((appId+":"+appSecret).getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty("Authorization", "Basic "+encoded);
            int code = conn.getResponseCode();
            if (code == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()))) {
                    StringBuffer buff = new StringBuffer();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        buff.append(inputLine + "\n");

                    String json = buff.toString();
                    logger.trace("request " + reqUrl + " then response :\n" + json);

                    List<ConfigItem> configs = new ArrayList<ConfigItem>();
                    return jsonConvert.deserializeObject(json, configs.getClass());
                }
            } else {
                logger.warn("request " + reqUrl + " failed , the response status code is " + code);
                return  null;
            }
        }
        catch (Exception ex) {
            //log ERROR
            logger.error("when request " + reqUrl + " error .", ex);
        }
        return  null;
    }
}
