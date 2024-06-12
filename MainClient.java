import java.awt.EventQueue;
import java.io.IOException;
import java.net.Socket;

public class MainClient {
    private final static int PORT = 5005;
    private final static String SERVER = "10.2.1.149";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Socket socket = new Socket(SERVER, PORT);
                Window frame = new Window(socket, false);
                frame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
