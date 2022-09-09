package mjzhou.agileconfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OptionsTest {

    @Test
    void getAppId() {
        Options op = new Options("","","");
        assertEquals("", op.getAppId());

        Options op1 = new Options("","xxx","");
        assertEquals("xxx", op1.getAppId());

        Options op2 = new Options("",null,"");
        assertEquals("", op2.getAppId());
    }

    @Test
    void getNodes() {
    }

    @Test
    void getSecret() {
        Options op = new Options("","","");
        assertEquals("", op.getSecret());

        Options op1 = new Options("","xxx","xxx");
        assertEquals("xxx", op1.getSecret());

        Options op2 = new Options("",null,null);
        assertEquals("", op2.getSecret());
    }
}