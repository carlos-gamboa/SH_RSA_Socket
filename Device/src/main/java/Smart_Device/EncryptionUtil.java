package Smart_Device;

import lombok.val;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.*;

/**
 * Created by luka on 5.7.17..
 */
@SuppressWarnings("WeakerAccess")
public class EncryptionUtil {

    public static final String ALGORITHM = "RSA";

    public static final String PRIVATE_KEY_FILE = "private.key";

    public static final String PUBLIC_KEY_FILE = "public.key";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void generateKey() {
        try {
            val keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(1024);
            val key = keyGen.generateKeyPair();

            val privateKeyFile = new File(PRIVATE_KEY_FILE);
            val publicKeyFile = new File(PUBLIC_KEY_FILE);

            if (privateKeyFile.getParentFile() != null) {
                privateKeyFile.getParentFile().mkdirs();
            }
            privateKeyFile.createNewFile();

            if (publicKeyFile.getParentFile() != null) {
                publicKeyFile.getParentFile().mkdirs();
            }
            publicKeyFile.createNewFile();

            val publicKeyOS = new ObjectOutputStream(
                    new FileOutputStream(publicKeyFile));
            publicKeyOS.writeObject(key.getPublic());
            publicKeyOS.close();

            val privateKeyOS = new ObjectOutputStream(
                    new FileOutputStream(privateKeyFile));
            privateKeyOS.writeObject(key.getPrivate());
            privateKeyOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static byte[] encrypt(String text, PublicKey key) {
        byte[] cipherText = null;
        try {
            val cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public static String decrypt(byte[] text, PrivateKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] decryptedText;
        val cipher = Cipher.getInstance(ALGORITHM);

        cipher.init(Cipher.DECRYPT_MODE, key);
        decryptedText = cipher.doFinal(text);

        return new String(decryptedText);
    }

}
