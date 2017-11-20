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
 *              Carlos Portuguez Ubeda
 *              Ana Laura Vargas Ramírez
 *
 */
@Log
public class Hub {

    @Getter
    private int port;

    @Getter
    @Setter
    private int connectionDelay;

    /**
     * Creates a new Hub
     *
     * @param port Hub's port.
     * @param connectionDelay Connection's Delay.
     */
    public Hub(int port, int connectionDelay) {
        this.port = port;
        this.connectionDelay = connectionDelay;
    }

    /**
     * Listens to messages.
     *
     * @param onConnectListener OnConnectListener instance.
     */
    public void listen(@NonNull OnConnectListener onConnectListener) {
        try {
            val hubSocket = new ServerSocket(port);

            log.info("Started hub on port: " + Integer.toString(this.port));

            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    val device = hubSocket.accept();
                    log.info("Device " + device.getInetAddress() + " connected");

                    val objectInputStream = new ObjectInputStream(device.getInputStream());
                    val objectOutputStream = new ObjectOutputStream(device.getOutputStream());

                    onConnectListener.onConnect(Device.builder()
                            .socket(device)
                            .objectInputStream(objectInputStream)
                            .objectOutputStream(objectOutputStream)
                            .build());

                } catch (Exception ex) {
                    log.warning("Cannot connect device");
                    System.exit(1);
                }

                try {
                    Thread.sleep(this.connectionDelay);
                } catch (InterruptedException e) {
                    log.warning("Could not sleep for " + Integer.toString(this.connectionDelay) + " seconds");
                    System.exit(1);
                }
            }

        } catch (IOException e) {
            log.severe("Cannot start hub");
            System.exit(1);
        }

    }
}
