package Smart_Device;

import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

/**
 * Created by luka on 6.7.17..
 *
 * Adapted by   Carlos Gamboa Vargas
 *              Carlos Portuguez Ubeda
 *              Ana Laura Vargas Ramírez
 *
 */
@Log
public class Device_Handler implements OnConnectToHub {

    private PrivateKey myPrivateKey;
    private Boolean intruder;
    private Device device;

    /**
     * Creates a Device Handler to manage the messages.
     *
     * @param myPrivateKey Device's private key.
     */
    public Device_Handler(@NonNull PrivateKey myPrivateKey, Boolean intruder, Device device) {
        this.myPrivateKey = myPrivateKey;
        this.intruder = intruder;
        this.device = device;
    }

    /**
     * Overrides the onConnectToHub method
     *
     * @param hub The cluster's hub.
     */
    @Override
    public void onConnectToHub(@NonNull Hub hub) {
        startListenerThread(hub);
    }

    /**
     * Listens to the hub in case there are messages.
     *
     * @param hub Cluster's hub.
     */
    private void startListenerThread(@NonNull Hub hub) {
        new Thread(() -> {
            while (true) {
                try {
                    val receivedEncryptedMessage = (byte[]) hub.getObjectInputStream().readObject();
                    if (intruder) {
                        System.out.println("Mensaje interceptado");
                        System.out.println(new String(receivedEncryptedMessage, Charset.forName("UTF-8")));
                        //System.out.println("Replicando mensaje");
                        //device.getHub().getObjectOutputStream().writeObject(receivedEncryptedMessage);
                    }
                    try {
                        val decryptedMessage = EncryptionUtil.decrypt(receivedEncryptedMessage, myPrivateKey);
                        synchronized (System.out) {
                            System.out.println(decryptedMessage);
                        }
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ignored) {
                        System.exit(1);
                    }

                } catch (EOFException ex) {
                    log.severe("Hub closed connection");
                    break;
                } catch (ClassNotFoundException | IOException e) {
                    System.exit(1);
                }
            }
        }).start();
    }
}