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
import java.util.Scanner;

/**
 * Created by luka on 6.7.17..
 */
public class DeviceApplication {


    private static PublicKey readPublicKeyFromFile(@NonNull String path) {
        try {
            return (PublicKey) new ObjectInputStream(new FileInputStream(path)).readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static PrivateKey readPrivateKeyFromFile(@NonNull String path) {
        try {
            return (PrivateKey) new ObjectInputStream(new FileInputStream(path)).readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static void main(String args[]) throws Exception {
        val sc = new Scanner(System.in);

        System.out.print("Hostname: ");
        val hostname = sc.nextLine();

        System.out.print("Port: ");
        val port = sc.nextInt();
        sc.nextLine();

        System.out.print("Username: ");
        val username = sc.nextLine();

        System.out.print("Your private key location: ");
        val privateKeyLocation = sc.nextLine();

        System.out.print("Peer's public key location: ");
        val publicKeyLocation = sc.nextLine();

        val peersPublicKey = readPublicKeyFromFile(publicKeyLocation);
        val myPrivateKey = readPrivateKeyFromFile(privateKeyLocation);


        val client = new Device(hostname, port);
        client.connectToServer(new Device_Handler(myPrivateKey));

        //noinspection InfiniteLoopStatement
        while (true) {
            synchronized (System.in) {
                String rawText = username + ": " + sc.nextLine();

                val encryptedText = EncryptionUtil.encrypt(rawText, peersPublicKey);
                client.getServer().getObjectOutputStream().writeObject(encryptedText);
            }
        }
    }
}
