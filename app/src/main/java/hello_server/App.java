package hello_server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class App {
    public static void main(String[] args) throws IOException {
        int port = 7878;
        ServerSocket listener = new ServerSocket();
        listener.bind(new InetSocketAddress("127.0.0.1", port));

        while (true) {
            Socket stream = listener.accept();
            System.out.println("Conenction established!");
        }
    }
}
