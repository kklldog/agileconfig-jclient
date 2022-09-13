package mjzhou.agileconfig;

import com.fasterxml.jackson.core.type.TypeReference;

public interface IJsonConvert {
   <T> T deserializeObject(String json, TypeReference<T> typeReference);

   String serializeObject(Object object);
}
