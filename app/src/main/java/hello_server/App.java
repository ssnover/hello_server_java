package hello_server;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws IOException {
        int port = 7878;
        ServerSocket listener = new ServerSocket();
        listener.bind(new InetSocketAddress("127.0.0.1", port));

        ThreadPool pool = new ThreadPool(4);

        for (var i = 0; i < 10; ++i) {
            Socket stream = listener.accept();
            pool.execute(new ConnectionHandler(stream));
        }

        System.out.println("Shutting down.");

        try {
            pool.finalize();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        listener.close();
    }

    static class ConnectionHandler implements Runnable {
        private final Socket stream;

        public ConnectionHandler(Socket stream) {
            this.stream = stream;
        }

        @Override
        public void run() {
            try {
                var buf_reader = new BufferedReader(new InputStreamReader(stream.getInputStream()));
                String request_line = buf_reader.readLine();

                String status_line = null;
                String filename = null;

                if (request_line.equals("GET / HTTP/1.1")) {
                    status_line = "HTTP/1.1 200 OK";
                    filename = "hello.html";
                } else if (request_line.equals("GET /sleep HTTP/1.1")) {
                    try {
                        int duration_ms = 5000;
                        Thread.sleep(duration_ms);
                    } catch (InterruptedException ex) {
                        System.exit(1);
                    }
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
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }
}
