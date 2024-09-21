package client_server;

import client_server.security.KeyPairManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;


public class ThreadedServer {
    private ServerSocket serverSocket;
    private List<User> activeUsers;
    private int tcpPort = 27999;
    private boolean clientConnected = false;
    private User userFromClient;
    private User userInvited;

    /**
     * Initialisiert den Server Socket (TCP) auf dem angegebenen Port,
     * hört auf eine Verbindungsanfrage eines Clients
     *
     * sowie die Liste aktiver User
     */
    public ThreadedServer() {
        try {
            this.serverSocket = new ServerSocket(this.tcpPort);
            this.activeUsers = new LinkedList<>();
            System.out.println("Warte auf Client...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialisiert Clientverbindung und streams zum Client, startet einen Verbindungs-Thread der dauerhaft läuft
     *
     * accept: Listens for a connection to be made to this socket and accepts it.
     * socket: Verbindung zu Client, erstellt in accept()
     */
    public void run_forever() {
        while (true) {
            try {
                Socket socket = this.serverSocket.accept();
                System.out.println("Client hat sich verbunden: " + socket.getInetAddress());

                final Thread thread = new Thread(() -> {
                    BufferedReader readerFromClient = null;
                    BufferedWriter writerToClient = null;
                    try {
                        readerFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        writerToClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handleRequests(readerFromClient, writerToClient, socket);
                });
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Empfängt Clientnachricht + schickt eine Response zurück
     */
    private void handleRequests(BufferedReader reader, BufferedWriter writer, Socket socket) {
        String line;
        while (!clientConnected) {
            try {
                line = reader.readLine();
                System.out.println("Nachricht von Client angekommen: " + line);
                if (line.startsWith(Protokoll.REGISTER.getText())) {
                    System.out.println("Checking Registration");
                    checkRegistration(line, writer);
                } else if (line.startsWith(Protokoll.LOGIN.getText())) {
                    System.out.println("Checking Login");
                    checkLogin(line, writer, socket);
                } else if (line.startsWith(Protokoll.REQUEST_LIST.getText())) {
                    System.out.println("Getting List of active users");
                    sendList(line, writer, reader);
                } else if (line.startsWith(Protokoll.REQUEST_INVITATION.getText())) {
                    System.out.println("Sending Invitation to other user");
                    sendInvitation(line, writer, reader);
                } else if (line.contains(Protokoll.INVITATION_ACCEPTED.getText()) && line.contains(Protokoll.IS_OK.getText())) {
                    informInvitingUserOverDecision(true, line);
                    sendInvitationAcceptedToFriend(line);
                    clientConnected = true;
                    break;
                } else if (line.contains(Protokoll.INVITATION_ACCEPTED.getText()) && line.contains(Protokoll.NOT_OK.getText())) {
                    informInvitingUserOverDecision(false, line);       //notify friend that invitation declined
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * informiert den einladenden User, ob die Einladung angenommen wurde oder nicht
     * nutzt Socket, sockets kommunizieren miteinander
     */
    private void informInvitingUserOverDecision(boolean invitationAccepted, String line) throws IOException {
        String usernameClient = line.split(" ")[2];
        String usernameInvited = line.split(" ")[3];
        User userFromClient = getUserByUsername(usernameClient);
        String response = "";

        //inform that invitation accepted
        Socket userFromClientSocket = userFromClient.getSocket();
        BufferedWriter writerToClient = new BufferedWriter(new OutputStreamWriter(userFromClientSocket.getOutputStream()));
        if (invitationAccepted) {
            response = Protokoll.INVITATION_ACCEPTED.getText() + " " + Protokoll.IS_OK.getText() + " " + usernameInvited;
        } else {
            response = Protokoll.INVITATION_ACCEPTED.getText() + Protokoll.NOT_OK.getText() + " " + usernameInvited;
        }
        sendResponse(writerToClient, response);
    }

    /**
     * einladender User wird benachrichtigt, dass invitation accepted wurde
     * an anfrager + freund werden die jeweiligen port infos geschickt
     */
    private void sendInvitationAcceptedToFriend(String line) throws IOException {
        String usernameClient = line.split(" ")[2];
        String usernameInvited = line.split(" ")[3];
        userFromClient = getUserByUsername(usernameClient);
        userInvited = getUserByUsername(usernameInvited);

        //send information to client
        assert userInvited != null;
        assert userFromClient != null;

        String response = getNewFriendsInformation(userInvited);
        Socket userFromClientSocket = userFromClient.getSocket();
        BufferedWriter writerToClient = new BufferedWriter(new OutputStreamWriter(userFromClientSocket.getOutputStream()));
        sendResponse(writerToClient, response);

        //send information to friend
        response = getNewFriendsInformation(userFromClient);
        Socket userInvitedSocket = userInvited.getSocket();
        BufferedWriter writerToInvitedClient = new BufferedWriter(new OutputStreamWriter(userInvitedSocket.getOutputStream()));
        sendResponse(writerToInvitedClient, response);
    }

    /**
     * checkt ob man sich mit den daten registrieren kann
     */
    private void checkRegistration(String request, BufferedWriter writer) {
        String username = request.split(" ")[1];
        String password = request.split(" ")[2];
        if (checkIfUserRegistered(username, password)) {
            sendResponse(writer, Protokoll.REGISTER.getText() + " " + Protokoll.NOT_OK.getText());
        } else {
            sendResponse(writer, Protokoll.REGISTER.getText() + " " + Protokoll.IS_OK.getText());
            saveInFile(username, password);
        }
    }

    /**
     * Checkt, ob der User in der Datei (nicht sicher) bereits existiert
     * Info: PW/Usernamen müssen gehasht + gesalzen sein
     */
    private boolean checkIfUserRegistered(String username, String password) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("./authenticated-user.txt"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String acceptedUserName = line.split(",")[0];
                String acceptedUserPassword = line.split(",")[1];

                if (username.equals(acceptedUserName) && password.equals(acceptedUserPassword)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Neuen User in Datei speichern (ungesichert)
     */
    private void saveInFile(String username, String password) {
        try (FileWriter fileWriter = new FileWriter("./authenticated-user.txt", true)) {
            fileWriter.write(username + "," + password + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checkt ob sich ein User einloggen kann mit den eingegebenen Daten
     * Info: es wird ein public key übertragen (erhält man vom client)!
     */
    private void checkLogin(String request, BufferedWriter writer, Socket socket) throws Exception {
        String username = request.split(" ")[1];
        String password = request.split(" ")[2];
        int port = Integer.parseInt(request.split(" ")[3]);
        String ipAddress = request.split(" ")[4];
        String publicKeyAsString = request.split(" ")[5];
        PublicKey publicKey = KeyPairManager.stringToPublicKey(publicKeyAsString);

        if (checkIfUserRegistered(username, password)) {
            sendResponse(writer, Protokoll.LOGIN.getText() + " " + Protokoll.IS_OK.getText());
            activeUsers.add(new User(username, password, port, ipAddress, socket, publicKey));
        } else {
            sendResponse(writer, Protokoll.LOGIN.getText() + " " + Protokoll.NOT_OK.getText());
        }
    }

    /**
     * Sendet eine Liste aktiver User
     */
    private void sendList(String request, BufferedWriter writer, BufferedReader reader) throws IOException {
        String username = request.split(" ")[1];
        String response = Protokoll.REQUEST_LIST.getText() + " " + getActiveUsersList(username);
        sendResponse(writer, response);
    }

    /**
     * Erstellt eine Response mit Infos über Freund (username, port, ip, public key)
     */
    private String getNewFriendsInformation(User newFriend) {
        String response = "";

        String newFriendName = newFriend.getUsername();
        int port = newFriend.getPort();
        String inetAddress = newFriend.getInetAddress();
        PublicKey publicKey = newFriend.getPublicKey();
        String publicKeyAsString = KeyPairManager.publicKeyToString(publicKey);

        response += Protokoll.INVITATION_ACCEPTED.getText() + " " + newFriendName + " " + port + " " + inetAddress +
                " " + publicKeyAsString;
        return response;
    }

    /**
     * Holt einen User nach Usernamen
     */
    private User getUserByUsername(String username) {
        for (User user : activeUsers) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Erstellt Liste aktiver User
     */
    private String getActiveUsersList(String clientName) {
        StringBuilder lst = new StringBuilder();
        for (User user : activeUsers) {
            if (user.getUsername().equals(clientName)) {
                lst.append("you[" + clientName + "]").append(",");
            } else {
                lst.append(user.getUsername()).append(",");
            }
        }
        return lst.toString();
    }

    /**
     * Sendet Einladungsanfrage zum anderen client
     */
    private void sendInvitation(String request, BufferedWriter writer, BufferedReader reader) throws IOException {
        String usernameClient = request.split(" ")[1];
        String usernameInvited = request.split(" ")[2];
        User userFromClient = getUserByUsername(usernameClient);
        User userInvited = getUserByUsername(usernameInvited);

        askForPermissionOfOtherClient(usernameClient, usernameInvited);

        String response = Protokoll.RECEIVED_INVITATION.getText() + " " + Protokoll.IS_OK.getText() + " " + usernameClient;

        Socket invitedUserSocket = userInvited.getSocket();
        BufferedWriter writerToInvitedClient = new BufferedWriter(new OutputStreamWriter(invitedUserSocket.getOutputStream()));
        sendResponse(writerToInvitedClient, response);
    }

    /**
     * Setzt nach Chatbestätigung usernamen der clients.
     *
     * @param usernameFromClient
     * @param usernameFromFriend
     */
    private void askForPermissionOfOtherClient(String usernameFromClient, String usernameFromFriend) {
        User friend = getUserByName(usernameFromFriend);
        User client = getUserByName(usernameFromClient);

        // set client
        friend.setPotentialChatPartnerName(client.getUsername());
        friend.setHasReceivedInvitation(true);
        // set friend
        client.setInvitingUser(friend.getUsername());
        client.setHasInvitedUser(true);
    }

    private User getUserByName(String name) {
        for (User user : activeUsers) {
            if (user.getUsername().equals(name)) {
                return user;
            }
        }
        return null;
    }

    private void sendResponse(BufferedWriter writer, String message) {
        try {
            System.out.println("Sendet Rückantwort: " + message);
            writer.write(message + "\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ThreadedServer ts = new ThreadedServer();
        ts.run_forever();
    }
}
