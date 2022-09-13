package mjzhou.agileconfig;

public interface IJsonConvert {
   public <T> T deserializeObject(String json , Class<T> var2);

   String serializeObject(Object object);
}
