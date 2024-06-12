import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {
    private final static int PORT = 5006;
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Waiting for connection...");
                Socket socket = serverSocket.accept();
                System.out.println("Connection established");

                Window frame = new Window(socket, true);
                frame.setVisible(true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
