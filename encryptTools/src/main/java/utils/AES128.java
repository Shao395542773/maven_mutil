package utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;

/***
 *
 * AES加密算法在java中只支持128位 16个字节的的key
 * AES 加密 cbc模式 pkcs7padding填充算法
 *
 *
 *
 */
public class AES128 {
    // 算法名称
    private static final String KEY_ALGORITHM = "AES";
    // 加解密算法/模式/填充方式
    private static final String algorithmStr = "AES/CBC/PKCS7Padding";
    private static Key key;
    private static Cipher cipher;
    private static final byte[] iv = {0x30, 0x31, 0x30, 0x32, 0x30, 0x33, 0x30, 0x34, 0x30, 0x35, 0x30, 0x36, 0x30, 0x37, 0x30, 0x38};

    private static void init(byte[] keyBytes) {

        // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
        int base = 16;
        if (keyBytes.length % base != 0) {
            int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
            keyBytes = temp;
        }
        // 初始化
        Security.addProvider(new BouncyCastleProvider());
        // 转化成JAVA的密钥格式
        key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        try {
            // 初始化cipher
            cipher = Cipher.getInstance(algorithmStr, "BC");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * 加密方法
     *
     * @param content  要加密的字符串
     * @param keyBytes 加密密钥
     * @return
     */
    public static byte[] encrypt(byte[] content, byte[] keyBytes) {
        byte[] encryptedText = null;
        init(keyBytes);
        System.out.println("IV：" + new String(iv));
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(content);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return encryptedText;
    }

    /**
     * 解密方法
     *
     * @param encryptedData 要解密的字符串
     * @param keyBytes      解密密钥
     * @return
     */
    public static byte[] decrypt(byte[] encryptedData, byte[] keyBytes) {
        byte[] encryptedText = null;
        init(keyBytes);
        System.out.println("IV：" + new String(iv));
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(encryptedData);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return encryptedText;
    }

}
