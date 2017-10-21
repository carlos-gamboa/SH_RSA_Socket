import Smart_Device.Device;
import Smart_Device.Device_Handler;
import Smart_Device.EncryptionUtil;
import lombok.NonNull;
import lombok.val;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by luka on 6.7.17..
 *
 * Adapted by   Carlos Gamboa Vargas
 *              Carlos Portuguez Ubeda
 *              Ana Laura Vargas Ramírez
 *
 */
public class DeviceApplication {


    private static String[] genericNames;
    private static Random randomGenerator;
    private static String device_key_path = "C:\\Users\\Dell\\Documents\\Universidad\\Redes de Computadores\\SH_RSA_Socket\\Device\\Device_Keys\\";
    private static String hub_key_path = "C:\\Users\\Dell\\Documents\\Universidad\\Redes de Computadores\\SH_RSA_Socket\\Hub\\Hub_Keys\\";

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
     * Reads a private key from a file
     *
     * @param path Path to the private key file/
     * @return PrivateKey|null
     */
    private static PrivateKey readPrivateKeyFromFile(@NonNull String path) {
        try {
            return (PrivateKey) new ObjectInputStream(new FileInputStream(path)).readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

    private static String generateRandomUsername () {
        String sensorName = genericNames[randomGenerator.nextInt(genericNames.length)];
        sensorName = sensorName + "-" + String.valueOf(randomGenerator.nextInt(200));
        return sensorName;
    }

    public static void main(String args[]) throws Exception {
        genericNames = new String[]{"MCE", "WTR", "IS", "IR", "HR"};
        randomGenerator = new Random();

        val sc = new Scanner(System.in);

        //System.out.print("Hostname: ");
        //val hostname = sc.nextLine();

        val hostname = "localhost";

        //System.out.print("Port: ");
        //val port = sc.nextInt();
        //sc.nextLine();
        val port = 8080;

        //System.out.print("Username: ");
        //val username = sc.nextLine();
        val username = generateRandomUsername();

        System.out.print("Your private key filename: ");
        val privateKeyLocation = device_key_path + sc.nextLine();

        System.out.print("Hub's public key filename: ");
        val publicKeyLocation = hub_key_path + sc.nextLine();

        val peersPublicKey = readPublicKeyFromFile(publicKeyLocation);
        val myPrivateKey = readPrivateKeyFromFile(privateKeyLocation);


        val device = new Device(hostname, port);
        device.connectToServer(new Device_Handler(myPrivateKey));

        //noinspection InfiniteLoopStatement
        while (true) {
            synchronized (System.in) {
                String rawText = username + ": " + sc.nextLine();

                val encryptedText = EncryptionUtil.encrypt(rawText, peersPublicKey);
                device.getHub().getObjectOutputStream().writeObject(encryptedText);
            }
        }
    }
}
