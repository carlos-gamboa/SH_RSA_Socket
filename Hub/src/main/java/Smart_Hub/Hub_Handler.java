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

    private String device_keys_path = "C:\\Users\\Dell\\Documents\\Universidad\\Redes de Computadores\\SH_RSA_Socket\\Device\\Device_Keys\\";

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
            System.out.print("Could not find the public key file.\n");
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
            log.warning("Received a message from " + name + ", who's not in your cluster.");
            System.out.print("Insert " + name + "'s public key filename. If you don't want to add this device to the cluster, insert \'No\'\n");
            val publicKeyFilename = sc.nextLine();
            val publicKeyLocation = device_keys_path + publicKeyFilename;
            if (!publicKeyFilename.equals("No")) {
                val devicesPublicKey = readPublicKeyFromFile(publicKeyLocation);
                if (devicesPublicKey == null) {
                    return false;
                }
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
                    System.out.print(decryptedMessage);
                }
                sendResponseMessage(getDeviceName(decryptedMessage));
                checkMessageKnown(decryptedMessage);
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
        return EncryptionUtil.encrypt("Hub: " + message, device_keys.get(device_Name));
    }

    /**
     * Sends a message.
     * @param device_Name Name of the device you want to send the message.
     */
    private void sendResponseMessage (String device_Name) {
        byte[] responseMessage = encryptResponseMessage("Message received", device_Name);
        broadcast(responseMessage);
    }

    /**
     * Checks if the message is a command.
     *
     * @param message Message received.
     */
    private void checkMessageKnown (String message) {
        StringTokenizer tokens = new StringTokenizer(message, ":");
        String name = tokens.nextToken();
        String full_message = tokens.nextToken();
        String data = "";
        StringTokenizer command_Tokens = new StringTokenizer(full_message, "!");
        String command = command_Tokens.nextToken();
        if (command_Tokens.hasMoreTokens()) {
             data = command_Tokens.nextToken();
        }
        if (command.equals(" Send") && !data.equals("")) {
            String final_Message = name + " sends the following data:" + data;
            byte[] responseMessage = encryptResponseMessage(final_Message, getRandomDeviceName(name));
            broadcast(responseMessage);
        }
    }

    /**
     * Gets a random device name.
     *
     * @param deviceName The name of the sender device.
     * @return String with the random device name.
     */
    private String getRandomDeviceName(String deviceName) {
        Random randomGenerator = new Random();
        List<String> keys = new ArrayList<String>(device_keys.keySet());
        String randomKey;
        do {
            randomKey = keys.get(randomGenerator.nextInt(keys.size()) );
        } while (deviceName.equals(randomKey));
        return randomKey;
    }
}