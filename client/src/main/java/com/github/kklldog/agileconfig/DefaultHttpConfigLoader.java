package com.github.kklldog.agileconfig;

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class DefaultHttpConfigLoader implements IConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(DefaultHttpConfigLoader.class);
    private static final IJsonConvert jsonConvert = new DefaultJsonConvert();
    private static final int defaultTimeoutSeconds = 100;

    @Override
    public List<ConfigItem> getConfigs(String node, String appId, String appSecret, String env) {
        ConfigLoadResult result = load(node, appId, appSecret, env, defaultTimeoutSeconds);

        return result == null ? null : result.getConfigs();
    }

    @Override
    public ConfigLoadResult load(String node, String appId, String appSecret, String env, int timeoutSeconds) {
        if (!node.endsWith("/")) {
            node += "/";
        }
        String reqUrl = node + "api/config/app/" + urlEncode(appId) + "?env=" + urlEncode(env);
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //set basic auth header .
            String encoded = Base64.getEncoder().encodeToString((appId + ":" + appSecret).getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty("appid", urlEncode(appId));
            conn.setRequestProperty("Authorization", "Basic " + encoded);
            int timeout = (timeoutSeconds <= 0 ? 30 : timeoutSeconds) * 1000;
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            int code = conn.getResponseCode();
            if (code == 200) {
                String publishTimeLineId = conn.getHeaderField(Protocol.HeaderKeyPublishTimeLineId);
                try (BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        sb.append(inputLine + "\n");

                    String json = sb.toString();
                    logger.trace("request " + reqUrl + " then response :\n" + json);

                    List<ConfigItem> configs = jsonConvert.deserializeObject(json, new TypeReference<List<ConfigItem>>() {
                    });
                    if (configs == null) {
                        return null;
                    }

                    return new ConfigLoadResult(configs, json, publishTimeLineId);
                }
            } else {
                logger.warn("request " + reqUrl + " failed , the response status code is " + code);
                return null;
            }
        } catch (Exception ex) {
            //log ERROR
            logger.error("when request " + reqUrl + " error .", ex);
        }
        return null;
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return value;
        }
    }
}
