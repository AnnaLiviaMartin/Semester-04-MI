package client_server;

import exceptions.WrongParameterException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadedServer {
    private final String OK = "200";
    private final String BAD_REQUEST = "400";
    private boolean userIsAccepted;
    private boolean connected;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private ServerSocket serverSocket;

    public ThreadedServer() {
        try {
            this.serverSocket = new ServerSocket(17);
            System.out.println("Warte auf client_server.Client ...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        final ThreadedServer s = new ThreadedServer();
        s.run_forever();
    }

    public void run_forever() throws IOException {
        while (true) {
            try {
                this.socket = this.serverSocket.accept();
                this.connected = true;
                System.out.printf("client_server.Client hat sich verbunden: %s%n", socket.getRemoteSocketAddress());
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!this.userIsAccepted) {
                String logInCredentials = reader.readLine();
                System.out.println("Authentication happens with input: " + logInCredentials);

                userIsAccepted = checkUserAuthentification(logInCredentials);
                if (userIsAccepted) {
                    writer.write(OK + "\n");
                    writer.flush();

                    System.out.println("User authenticated.");
                } else {
                    writer.write(BAD_REQUEST + "\n");
                    writer.flush();
                }
            }
            handleRequests(socket);
        }
    }

    private void handleRequests(final Socket socket) {
        while (this.connected) {

            try {
                String line = reader.readLine();
                if (line != null) {
                    System.out.printf("Vom client_server.Client (%s) empfangen: %s%n", socket.getRemoteSocketAddress(), line);
                    System.out.printf("Sende an client_server.Client (%s): %s%n", socket.getRemoteSocketAddress(),
                            calculator(line));

                    writer.write(calculator(line) + "\n");
                    writer.flush();
                } else {
                    connected = false;
                }
            } catch (IOException | WrongParameterException e) {
                //writer.write does not go through, when exception is thrown
                //writer.write(BAD_REQUEST + "\n");
                System.out.println(e.getMessage());
                return;
            }
        }
    }

    private String calculator(String operation) throws WrongParameterException, IOException {
        String[] tokens = operation.split(" ");
        double result = 0;
        System.out.println("Trying to calculate: " + operation);

        if (tokens.length != 5 || !operation.split(" ")[0].equals("CALC")) {
            return (BAD_REQUEST + "\n");
        } else {
            double num1 = Integer.parseInt(tokens[1]);
            double num2 = Integer.parseInt(tokens[3]);

            switch (tokens[2]) {
                case "+" -> result = num1 + num2;
                case "-" -> result = num1 - num2;
                case "*" -> result = num1 * num2;
                case "/" -> result = num1 / num2;
            }
        }
        return OK + ", " + operation + " " + result;
    }

    private boolean checkUserAuthentification(String logInCredentials) {
        final String userName;
        final String userPassword;

        //check LOGIN
        String method = logInCredentials.split(" ")[0];
        if(!method.equals("LOGIN")) return false;

        if (logInCredentials != null & logInCredentials.split(",").length == 2) {
            userName = logInCredentials.split(" ")[1].split(",")[0];
            userPassword = logInCredentials.split(" ")[1].split(",")[1];
        } else return false;

        System.out.println("User " + userName + " tries to logg in.");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("resources/user.txt"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String acceptedUserName = line.split(",")[0];
                String acceptedUserPassword = line.split(",")[1];

                if (userName.equals(acceptedUserName) && userPassword.equals(acceptedUserPassword)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
