package hello_server;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        String request_line = buf_reader.readLine();

        String status_line = null;
        String filename = null;

        if (request_line.equals("GET / HTTP/1.1")) {
            status_line = "HTTP/1.1 200 OK";
            filename = "hello.html";
        } else {
            status_line = "HTTP/1.1 404 NOT FOUND";
            filename = "404.html";
        }

        String contents = Files.readString(Paths.get(filename));
        var length = contents.length();

        String response = String.format("%s\r\nContent-Length: %d\r\n\r\n%s", status_line, length, contents);
        stream.getOutputStream().write(response.getBytes());

        stream.close();
    }
}
