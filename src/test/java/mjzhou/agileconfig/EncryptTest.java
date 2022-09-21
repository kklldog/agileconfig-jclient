package mjzhou.agileconfig;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class EncryptTest {

    @Test
    void md5() {
        String source = "a&g:a1&b&b1";
        String md5str = Encrypt.md5(source);

        assertEquals("06516CD929B87FAB37FA0E19199AFB8C", md5str.toUpperCase(Locale.ROOT));
    }
}