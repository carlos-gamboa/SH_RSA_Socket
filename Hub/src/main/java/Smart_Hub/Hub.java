package Smart_Hub;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;
import lombok.val;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;

/**
 * Created by luka on 6.7.17..
 *
 * Adapted by   Carlos Gamboa Vargas
 *              Carlos Portuguéz Ubeda
 *              Ana Laura Vargas
 *
 */

@Log
public class Hub {

    @Getter
    private int port;

    @Getter
    @Setter
    private int connectionDelay;

    public Hub(int port, int connectionDelay) {
        this.port = port;
        this.connectionDelay = connectionDelay;
    }

    public void listen(@NonNull OnConnectListener onConnectListener) {
        try {
            val serverSocket = new ServerSocket(port);

            log.info("Started server on port: " + Integer.toString(this.port));

            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    val client = serverSocket.accept();
                    log.info("Client " + client.getInetAddress() + " connected");

                    val objectInputStream = new ObjectInputStream(client.getInputStream());
                    val objectOutputStream = new ObjectOutputStream(client.getOutputStream());

                    onConnectListener.onConnect(Device.builder()
                            .socket(client)
                            .objectInputStream(objectInputStream)
                            .objectOutputStream(objectOutputStream)
                            .build());

                } catch (Exception ex) {
                    log.warning("Cannot connect client");
                    ex.printStackTrace();
                }

                try {
                    Thread.sleep(this.connectionDelay);
                } catch (InterruptedException e) {
                    log.warning("Could not sleep for " + Integer.toString(this.connectionDelay) + " seconds");
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            log.severe("Cannot start server");
            e.printStackTrace();
        }

    }
}
