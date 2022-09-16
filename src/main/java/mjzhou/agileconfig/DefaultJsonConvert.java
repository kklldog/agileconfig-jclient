package mjzhou.agileconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class DefaultJsonConvert implements IJsonConvert {
    private static final ObjectMapper mapper = new ObjectMapper();

    public DefaultJsonConvert(){
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

    }

    @Override
    public <T> T deserializeObject(String json, TypeReference<T> typeReference) {
         try {
             return mapper.readValue(json, typeReference);
         }
         catch (Exception ex) {
            ex.printStackTrace();
         }

         return  null;
    }

    @Override
    public String serializeObject(Object object){
        try {
            String json = mapper.writeValueAsString(object);

            return json;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }
}
