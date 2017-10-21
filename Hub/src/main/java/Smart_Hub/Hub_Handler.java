package Smart_Hub;

import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

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

    /**
     * Creates a new Hub_Handler
     */
    public Hub_Handler() {
        devices = new ArrayList<>();
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
     * Starts a new Device thread.
     *
     * @param device The Device you want to start.
     */
    private void startClientThread(@NonNull Device device) {
        new Thread(() -> {
            while (true) {
                try {
                    val bytesSentByClient = (byte[]) device.getObjectInputStream().readObject();
                    broadcast(bytesSentByClient);
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
}