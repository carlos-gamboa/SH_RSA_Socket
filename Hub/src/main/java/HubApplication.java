import Smart_Hub.Hub;
import Smart_Hub.Hub_Handler;
import lombok.NonNull;
import lombok.val;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.util.Scanner;

/**
 * Created by luka on 6.7.17..
 *
 * Adapted by   Carlos Gamboa Vargas
 *              Carlos Portuguez Ubeda
 *              Ana Laura Vargas Ramírez
 *
 */
public class HubApplication {

    private static String hub_keys_path = "C:\\Users\\Usuario1\\IntelliJIdeaProjects\\SH_RSA_Socket\\Hub\\Hub_Keys\\private.key";

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
            System.out.print("Could not find the private key file.\nInsert Hub's private key filename.\n");
        }

        return null;
    }

    public static void main(String[] args) {
        val sc = new Scanner(System.in);
        PrivateKey private_key = null;

        //System.out.print("Hub's private key filename: ");
        do {
            val privateKeyLocation = hub_keys_path;// + sc.nextLine();
            private_key = readPrivateKeyFromFile(privateKeyLocation);
        }while (private_key == null);

        val server = new Hub(8080, 250);
        server.listen(new Hub_Handler(private_key));
    }
}
