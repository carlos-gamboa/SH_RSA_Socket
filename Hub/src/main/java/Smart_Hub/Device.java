package Smart_Hub;

import lombok.Builder;
import lombok.Data;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by luka on 6.7.17..
 */
@Data
@Builder
class Device {

    private Socket socket;

    private ObjectOutputStream objectOutputStream;

    private ObjectInputStream objectInputStream;

}
