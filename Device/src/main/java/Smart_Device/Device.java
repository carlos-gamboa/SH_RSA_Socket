package Smart_Device;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by luka on 6.7.17..
 *
 * Adapted by   Carlos Gamboa Vargas
 *              Carlos Portuguez Ubeda
 *              Ana Laura Vargas Ramírez
 *
 */
@Log
public class Device {
    private String hostname;
    private int port;

    @Getter
    private Hub hub;

    /**
     * Creates a new Device
     *
     * @param hostname Device's hostname.
     * @param port Device's port.
     */
    public Device(@NonNull String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Connects the Device to a server
     *
     * @param onMessageReceivedListener OnConnectToHub instance.
     */
    public void connectToServer(@NonNull OnConnectToHub onMessageReceivedListener) {
        Socket hubSocket;
        try {
            hubSocket = new Socket(hostname, port);
            val objectOutputStream = new ObjectOutputStream(hubSocket.getOutputStream());
            val objectInputStream = new ObjectInputStream(hubSocket.getInputStream());

            hub = Hub.builder()
                    .objectInputStream(objectInputStream)
                    .socket(hubSocket)
                    .objectOutputStream(objectOutputStream)
                    .build();

            onMessageReceivedListener.onConnectToHub(hub);


        } catch (IOException e) {
            log.severe("Cannot connect to hub");
            e.printStackTrace();
        }
    }

}