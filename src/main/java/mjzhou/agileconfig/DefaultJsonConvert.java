package mjzhou.agileconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class DefaultJsonConvert implements IJsonConvert {
    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T deserializeObject(String json, Class<T> var2) {
         try {
             return mapper.readValue(json, var2);
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
