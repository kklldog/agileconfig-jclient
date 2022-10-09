package com.github.kklldog.agileconfig;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJsonConvert implements IJsonConvert {
    private static final Logger logger = LoggerFactory.getLogger(DefaultJsonConvert.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public DefaultJsonConvert() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

    }

    @Override
    public <T> T deserializeObject(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (Exception ex) {
            logger.error("deserializeObject json to Type " + typeReference.getType() + " error .", ex);
        }

        return null;
    }

    @Override
    public String serializeObject(Object object) {
        try {
            String json = mapper.writeValueAsString(object);

            return json;
        } catch (Exception ex) {
            logger.error("serializeObject object to json error .", ex);
        }

        return "";
    }
}
