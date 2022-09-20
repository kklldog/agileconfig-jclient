package mjzhou.agileconfig;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomServersTest {

    @Test
    void isComplete() {
        String[] servers = new String[]{
        };
        assertThrows(IllegalArgumentException.class, () -> {
            RandomServers rds = new RandomServers(servers);
        });
        String[] servers1 = new String[]{
                "http://"
        };
        RandomServers rds1 = new RandomServers(servers1);
        boolean cp1 = rds1.isComplete();
        assertTrue(!cp1);
        rds1.next();
        cp1 = rds1.isComplete();
        assertTrue(cp1);

        String val = rds1.next();
        assertEquals("", val);
    }

    @Test
    void next() {
        String[] servers = new String[]{
        };
        assertThrows(IllegalArgumentException.class, () -> {
            RandomServers rds = new RandomServers(servers);
        });
        String[] servers1 = new String[]{
                "http://"
        };
        RandomServers rds1 = new RandomServers(servers1);
        String str = rds1.next();
        assertEquals("http://", str);

        String[] servers2 = new String[]{
                "http://0",
                "http://11",
                "http://222",
        };
        RandomServers rds2 = new RandomServers(servers2);
        while (!rds2.isComplete()) {
            String str2 = rds2.next();
            System.out.println(str2);
        }

        RandomServers rds3 = new RandomServers(servers2);
        while (!rds3.isComplete()) {
            String str3 = rds3.next();
            System.out.println(str3);
        }
    }
}