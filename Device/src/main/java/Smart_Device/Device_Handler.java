package Smart_Device;

import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.EOFException;
import java.io.IOException;
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

    /**
     * Creates a Device Handler to manage the messages.
     *
     * @param myPrivateKey Device's private key.
     */
    public Device_Handler(@NonNull PrivateKey myPrivateKey) {
        this.myPrivateKey = myPrivateKey;
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
                    try {
                        val decryptedMessage = EncryptionUtil.decrypt(receivedEncryptedMessage, myPrivateKey);
                        synchronized (System.out) {
                            System.out.println(decryptedMessage);
                        }
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ignored) {

                    }

                } catch (EOFException ex) {
                    log.severe("Hub closed connection");
                    break;
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();
    }
}