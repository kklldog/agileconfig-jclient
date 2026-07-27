package com.github.kklldog.agileconfig;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class Encrypt {
    private static final String HEX_STRING = "0123456789abcdef";
    private static final char[] HEX_CHARS = HEX_STRING.toCharArray();
    public static String md5(String input) {
        return md5(input.getBytes(StandardCharsets.US_ASCII));
    }

    public static String md5(byte[] input) {
        MessageDigest md;
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

    /**
     * 由 secret 派生 AES 密钥: 对 secret 做两次 SHA-1, 取前 16 字节。
     * 与 C# 客户端的 Encrypt.AesEncryptECB 保持一致。
     */
    private static SecretKeySpec buildAesKey(String key) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hash = sha1.digest(key.getBytes(StandardCharsets.UTF_8));
            byte[] rd = sha1.digest(hash);

            return new SecretKeySpec(Arrays.copyOf(rd, 16), "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AES/ECB/PKCS5Padding 加密, 返回 base64 。与 C# 客户端兼容。
     */
    public static String aesEncryptECB(String key, String rawData) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, buildAesKey(key));
            byte[] result = cipher.doFinal(rawData.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AES/ECB/PKCS5Padding 解密。与 C# 客户端兼容。
     */
    public static String aesDecryptECB(String key, String base64Data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, buildAesKey(key));
            byte[] result = cipher.doFinal(Base64.getDecoder().decode(base64Data));

            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
