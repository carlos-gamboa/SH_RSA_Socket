import Smart_Hub.Hub;
import Smart_Hub.Hub_Handler;
import lombok.val;

/**
 * Created by luka on 6.7.17..
 */
public class HubApplication {

    public static void main(String[] args) {
        val server = new Hub(8080, 250);
        server.listen(new Hub_Handler());
    }
}
