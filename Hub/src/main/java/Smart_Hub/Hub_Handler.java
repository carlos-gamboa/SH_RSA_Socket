package Smart_Hub;

import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

/**
 * Created by luka on 6.7.17..
 *
 * Adapted by   Carlos Gamboa Vargas
 *              Carlos Portuguez Ubeda
 *              Ana Laura Vargas Ramírez
 *
 */
@Log
public class Hub_Handler implements OnConnectListener {

    private ArrayList<Device> devices;
    private Map<String, PublicKey> device_keys;
    private PrivateKey myPrivateKey;

    /**
     * Creates a Hub Handler to manage the messages.
     *
     * @param myPrivateKey Device's private key.
     */
    public Hub_Handler(@NonNull PrivateKey myPrivateKey) {
        devices = new ArrayList<>();
        device_keys = new HashMap<String, PublicKey>();
        this.myPrivateKey = myPrivateKey;
    }

    /**
     * Reads a public key from a file
     *
     * @param path Path to the public key file/
     * @return PublicKey|null
     */
    private static PublicKey readPublicKeyFromFile(@NonNull String path) {
        try {
            return (PublicKey) new ObjectInputStream(new FileInputStream(path)).readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Overrides onConnect method.
     *
     * @param device Device you want to connect.
     */
    @Override
    public void onConnect(@NonNull Device device) {
        devices.add(device);
        startClientThread(device);
    }

    /**
     * Checks if a Device is in the cluster. If it is not, it can add it to the cluster or reject it.
     *
     * @param message Message received by the Hub.
     * @return true if the device is part of the custer | false if the device is not part of the cluster.
     */
    private boolean associateDevicePublicKey(String message) {
        String name = getDeviceName(message);

        if (!device_keys.containsKey(name)) {
            val sc = new Scanner(System.in);

            System.out.print("Received a message from " + name + ", who's not in your cluster.");
            System.out.print("Insert " + name + "'s public key location. If you don't want to add this device to the cluster, insert \'No\'");
            val publicKeyLocation = sc.nextLine();
            if (!publicKeyLocation.equals("No")) {
                val devicesPublicKey = readPublicKeyFromFile(publicKeyLocation);
                device_keys.put(name, devicesPublicKey);
                return true;
            }
            else {
                System.out.print(name + " was not added to the cluster.");
                return false;
            }
        }
        else {
            return true;
        }
    }

    /**
     * Starts a new Device thread. This means, that listens to new messages from devices.
     *
     * @param device The Device you want to start.
     */
    private void startClientThread(@NonNull Device device) {
        new Thread(() -> {
            while (true) {
                try {
                    val bytesSentByClient = (byte[]) device.getObjectInputStream().readObject();
                    showMessage(bytesSentByClient);
                } catch (EOFException ex) {
                    log.warning("Client closed connection");
                    break;

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Broadcasts a message to all devices.
     *
     * @param bytes Message you want to broadcast.
     */
    private void broadcast(@NonNull byte[] bytes) {
        devices.forEach(device -> {
            try {
                device.getObjectOutputStream().writeObject(bytes);
            } catch (IOException e) {
                log.warning("Cannot send bytes to client: " + device.getSocket().getInetAddress() + ":" + device.getSocket().getPort());
                e.printStackTrace();
            }
        });
    }

    /**
     * Displays the received message.
     *
     * @param encryptedMessage Received message.
     */
    private void showMessage (@NonNull byte[] encryptedMessage) {
        try {
            val decryptedMessage = EncryptionUtil.decrypt(encryptedMessage, myPrivateKey);
            Boolean added = associateDevicePublicKey(decryptedMessage);
            if (added) {
                synchronized (System.out) {
                    System.out.println(decryptedMessage);
                }
                sendResponseMessage(getDeviceName(decryptedMessage));
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ignored) {

        }
    }

    /**
     * Gets the name of a device based on the message they send.
     *
     * @param message Message received by the hub.
     * @return Devices name.
     */
    private String getDeviceName(String message) {
        StringTokenizer tokens = new StringTokenizer(message, ":");
        return tokens.nextToken();
    }

    /**
     * Encrypts a response message
     *
     * @param message Message to be encrypted.
     * @param device_Name Name of the device you want to send the encrypted message.
     * @return Encrypted message.
     */
    private byte[] encryptResponseMessage (String message, String device_Name) {
        return EncryptionUtil.encrypt(message, device_keys.get(device_Name));
    }

    /**
     * Sends a message.
     * @param device_Name Name of the device you want to send the message.
     */
    private void sendResponseMessage (String device_Name) {
        byte[] responseMessage = encryptResponseMessage("Message received", device_Name);
        broadcast(responseMessage);
    }
}