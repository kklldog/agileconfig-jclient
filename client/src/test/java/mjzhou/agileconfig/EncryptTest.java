package mjzhou.agileconfig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptTest {

    @Test
    void md5() {
        String source = "a&g:a1&b&b1";
        String md5str = Encrypt.md5(source);
        assertEquals("06516CD929B87FAB37FA0E19199AFB8C", md5str.toUpperCase());

        source = "111&222&33&db:connection&Global:OptionsTest&groupx:hahha&ttttttt&11111&123&2222&22222草草草&333&test123123123姐姐11111&发生大发";
        md5str = Encrypt.md5(source);
        assertEquals("d53db6dda90280f21de7f87f1f437245", md5str);

    }
}