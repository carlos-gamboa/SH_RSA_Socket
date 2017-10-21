package Smart_Hub;

import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.val;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by luka on 6.7.17..
 */
@Log
public class Hub_Handler implements OnConnectListener {

    private ArrayList<Device> clients;

    public Hub_Handler() {
        clients = new ArrayList<>();
    }

    @Override
    public void onConnect(@NonNull Device client) {
        clients.add(client);
        startClientThread(client);
    }

    private void startClientThread(@NonNull Device client) {
        new Thread(() -> {
            while (true) {
                try {
                    val bytesSentByClient = (byte[]) client.getObjectInputStream().readObject();
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

    private void broadcast(@NonNull byte[] bytes) {
        clients.forEach(client -> {
            try {
                client.getObjectOutputStream().writeObject(bytes);
            } catch (IOException e) {
                log.warning("Cannot send bytes to client: " + client.getSocket().getInetAddress() + ":" + client.getSocket().getPort());
                e.printStackTrace();
            }
        });
    }
}