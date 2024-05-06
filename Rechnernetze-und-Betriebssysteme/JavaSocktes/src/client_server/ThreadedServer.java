package client_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadedServer {
    private ServerSocket serverSocket;

    public ThreadedServer() {
        try {
            this.serverSocket = new ServerSocket(27999);
            System.out.println("Warte auf Client...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run_forever() {
        while (true) {
            try {
                final Socket socket = this.serverSocket.accept();
                System.out.println("Client hat sich verbunden: " + socket.getInetAddress());

                final Thread thread = new Thread(() ->  {
					BufferedReader reader = null;
					BufferedWriter writer = null;

					try {
						reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					} catch (IOException e) {
						e.printStackTrace();
					}

					handleRequests(socket, reader, writer);
                });
				thread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    private void handleRequests(final Socket socket, final BufferedReader reader, final BufferedWriter writer) {
        boolean connected = true;

        while (connected) {
            final String line;
            try {
                line = reader.readLine();
                if (line != null) {
                    System.out.printf("Vom Client (%s) empfangen: %s%n", socket.getRemoteSocketAddress(), line);
                    final StringBuffer reverse = new StringBuffer(line).reverse();
                    System.out.printf("Sende an Client (%s): %s%n", socket.getRemoteSocketAddress(), reverse);
                    writer.write(reverse + "\n");
                    writer.flush();
                } else {
                    connected = false;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
    }

    public static void main(String[] args) {
		ThreadedServer ts =new ThreadedServer();
		ts.run_forever();
    }
}
