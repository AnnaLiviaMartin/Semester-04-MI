package client_server;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    public Client() {
        try {
            socket = new Socket("localhost", 17);
            System.out.println("client_server.Client connected end with \"END\"");

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Boolean authenticated = client.sendUserAuthentification();

        if (authenticated) {
            while (true) {
                System.out.println("Your calculation pls. Format: 'x + y ='");
                final String line = reader.readLine();
                if (line.equals("END")) {
                    break;
                }

                client.sendText(line);
            }
        }
        client.close();
    }

    public Boolean sendUserAuthentification() throws IOException {
        final BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        String consoleInput;
        String serverResult = " ";

        while (true) {
            System.out.println("Please Authenticate. Format: 'loginName,loginPassword'");
            consoleInput = consoleReader.readLine();
            writer.write("LOGIN " + consoleInput + "\n");
            writer.flush();

            serverResult = reader.readLine();
            System.out.println("ServerResponse: " + serverResult);

            if (serverResult.equals("200")) {
                System.out.println("You're authenticated.");
                return true;
            } else {
                System.out.println("Format or Credentials are wrong. Pls try again.");
            }
        }
    }

    public void sendText(final String text) {
        try {
            System.out.println("Send to Server: " + text);
            writer.write("CALC " + text + "\n");
            writer.flush(); //Leert den Writer

            final String line = reader.readLine();

            if(line.equals("400")){
                System.out.println("Format is not right. Please try again.");
            } else {
                System.out.println("Received from Server: " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            socket.shutdownOutput();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
