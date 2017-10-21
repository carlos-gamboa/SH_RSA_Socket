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
 *              Carlos Portuguéz Ubeda
 *              Ana Laura Vargas
 *
 */
@Log
public class Device {
    private String hostname;
    private int port;

    @Getter
    private Hub server;

    public Device(@NonNull String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void connectToServer(@NonNull OnConnectToHub onMessageReceivedListener) {
        Socket serverSocket;
        try {
            serverSocket = new Socket(hostname, port);
            val objectOutputStream = new ObjectOutputStream(serverSocket.getOutputStream());
            val objectInputStream = new ObjectInputStream(serverSocket.getInputStream());

            server = Hub.builder()
                    .objectInputStream(objectInputStream)
                    .socket(serverSocket)
                    .objectOutputStream(objectOutputStream)
                    .build();

            onMessageReceivedListener.onConnectToHub(server);


        } catch (IOException e) {
            log.severe("Cannot connect to server");
            e.printStackTrace();
        }
    }

}