package Smart_Device;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

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
@SuppressWarnings("WeakerAccess")
@Data
@Builder
public class Hub {

    private Socket socket;

    @Getter
    private ObjectOutputStream objectOutputStream;

    @Getter
    private ObjectInputStream objectInputStream;

}
