package mjzhou.agileconfig;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {
    private static final String HEX_STRING = "0123456789abcdef";
    private static final char[] HEX_CHARS = HEX_STRING.toCharArray();
    public static String md5(String input) {
        return md5(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String md5(byte[] input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(input);
        byte[] digest = md.digest();
        return toHexString(digest);
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b) {
            int hi = (x & 0xf0) >> 4;
            int lo = x & 0x0f;
            sb.append(HEX_CHARS[hi]);
            sb.append(HEX_CHARS[lo]);
        }
        return sb.toString().trim();
    }
}
