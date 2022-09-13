package mjzhou.agileconfig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultJsonConvertTest {

    @Test
    void deserializeObject() {
        DefaultJsonConvert convert = new DefaultJsonConvert();
        ConfigItem config = new ConfigItem();
        config.setGroup("a");
        config.setKey("b");
        config.setValue("c");
        String json = convert.serializeObject(config);
        assertNotNull(json);

        ConfigItem config2 = convert.deserializeObject(json,ConfigItem.class);
        assertNotNull(config2);
        assertEquals("a", config2.getGroup());
        assertEquals("b", config2.getKey());
        assertEquals("c", config2.getValue());
    }

    @Test
    void serializeObject() {
        DefaultJsonConvert convert = new DefaultJsonConvert();
        ConfigItem config = new ConfigItem();
        config.setGroup("a");
        config.setKey("b");
        config.setValue("c");
        String json = convert.serializeObject(config);

        System.out.println(json);

        assertNotNull(json);
    }
}