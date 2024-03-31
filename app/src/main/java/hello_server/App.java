package hello_server;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) throws IOException {
        int port = 7878;
        ServerSocket listener = new ServerSocket();
        listener.bind(new InetSocketAddress("127.0.0.1", port));

        while (true) {
            Socket stream = listener.accept();
            handle_connection(stream);
        }
    }

    public static void handle_connection(Socket stream) throws IOException {
        var buf_reader = new BufferedReader(new InputStreamReader(stream.getInputStream()));

        ArrayList<String> request_lines = new ArrayList<>();
        String line;
        while ((line = buf_reader.readLine()) != null && !line.isEmpty()) {
            request_lines.add(line);
        }
        System.out.println("Request: " + request_lines.toString());
        stream.close();
    }
}
