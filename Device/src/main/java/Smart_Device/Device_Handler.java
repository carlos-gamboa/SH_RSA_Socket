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
 */
@Log
public class Device_Handler implements OnConnectToHub {

    private PrivateKey myPrivateKey;

    public Device_Handler(@NonNull PrivateKey myPrivateKey) {
        this.myPrivateKey = myPrivateKey;
    }

    @Override
    public void onConnectToHub(@NonNull Hub server) {
        startListenerThread(server);

    }

    private void startListenerThread(@NonNull Hub server) {
        new Thread(() -> {
            while (true) {
                try {
                    val receivedEncryptedMessage = (byte[]) server.getObjectInputStream().readObject();
                    try {
                        val decryptedMessage = EncryptionUtil.decrypt(receivedEncryptedMessage, myPrivateKey);
                        synchronized (System.out) {
                            System.out.println(decryptedMessage);
                        }
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ignored) {

                    }

                } catch (EOFException ex) {
                    log.severe("Server closed connection");
                    break;
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();
    }
}