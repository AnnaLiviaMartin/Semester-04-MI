package client_server;

import client_server.security.KeyPairManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Scanner;

/**
 * Managed die Verbindung und Kommunikation zwischen den Client und dem Server her
 */
public class TcpClient {
    private Socket socket;
    private BufferedWriter writerToServer;
    private BufferedReader readerToServer;
    private Scanner readerFromTerminal;
    private boolean isLoggedIn = false;
    private String username;
    private int tcpPort = 27999;
    private int udpPortClient = 0;
    private String[] client_args;
    private boolean wasInvited = false;
    private boolean hasChatWithFriend = false;
    private String chatInformation = "";
    private String invitingUser;
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private UdpClient clientToClient;
    private String name;
    private int friendsPort;
    private String inetAddress;
    private KeyPairManager ownKeyPairManager;
    private PublicKey friendsPublicKey;

    /**
     * Initialisiert socket und started Sende und Empfang Threads.
     *
     * @param args den eigenen port
     */
    public TcpClient(String[] args) {
        try {
            this.client_args = args;
            this.udpPortClient = Integer.parseInt(client_args[0]);
            socket = new Socket("localhost", this.tcpPort);
            System.out.println("Client connected end with \"END\"");
            this.ownKeyPairManager = new KeyPairManager();

            writerToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            readerToServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            readerFromTerminal = new Scanner(System.in);

            //init threads
            startSendingThread();
            startReceivingThread();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void close() {
        try {
            writerToServer.close();
            readerToServer.close();
            readerFromTerminal.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void setWasInvited(boolean invited) {
        boolean oldVal = wasInvited;
        this.wasInvited = invited;
        changeSupport.firePropertyChange("wasInvited", oldVal, invited);
    }

    private synchronized boolean getWasInvited() {
        return wasInvited;
    }

    private synchronized void setHasChatWithFriend() {
        this.hasChatWithFriend = true;
        changeSupport.firePropertyChange("hasChatWithFriend", false, true);
    }

    private synchronized boolean getHasChatWithFriend() {
        return hasChatWithFriend;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Initialisierung der ChangeListener für die Informationen, ob eine Einladung geschickt wurde oder ein Chat
     * akzeptiert wurde
     */
    private class MyPropertyChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("wasInvited".equals(evt.getPropertyName())) {
                System.out.println("Eigenschaft 'wasInvited' wurde geändert von " + evt.getOldValue() + " zu " + evt.getNewValue());        //todo delete
                try {
                    if (getWasInvited()) {        // nur wenn man eingeladen wurde, wird man gefragt, ob man die Einladung annehmen möchte
                        askIfInvitationAccepted();
                        setWasInvited(false);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if ("hasChatWithFriend".equals(evt.getPropertyName())) {
                System.out.println("Eigenschaft 'hasChatWithFriend' wurde geändert von " + evt.getOldValue() + " zu " + evt.getNewValue());        //todo delete
                try {
                    if (getHasChatWithFriend()) {
                        setNewFriendsInformation(chatInformation);
                        try {
                            UdpClient clientToClient = new UdpClient(name, friendsPort, udpPortClient, username,
                                    ownKeyPairManager.getPrivateKey(), friendsPublicKey);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Startet den Receiving Thread
     * Zuständig für Empfang von serverseitigen Nachrichten
     */
    private void startReceivingThread() {
        new Thread(() -> {
            try {
                String response;
                while ((response = readerToServer.readLine()) != null && !getHasChatWithFriend()) {
                    System.out.println("Von Server erhalten: " + response);      //todo delete

                    if (response.contains(Protokoll.REGISTER.getText()) && response.contains(Protokoll.NOT_OK.getText())) {
                        System.out.println("So kannst du dich leider nicht registrieren.");
                    } else if (response.contains(Protokoll.REGISTER.getText()) && response.contains(Protokoll.IS_OK.getText())) {
                        System.out.println("Du hast dich erfolgreich registriert!");
                    }

                    if (response.contains(Protokoll.LOGIN.getText()) && response.contains(Protokoll.NOT_OK.getText())) {
                        System.out.println("Falscher Login. So kannst du dich leider nicht einloggen! Entweder dein Username/Passwort ist falsch oder du bist noch nicht registriert.");
                        isLoggedIn = false;
                    } else if (response.contains(Protokoll.LOGIN.getText()) && response.contains(Protokoll.IS_OK.getText())) {
                        System.out.println("Du bist jetzt eingeloggt.");
                        isLoggedIn = true;
                    }

                    if (response.contains(Protokoll.REQUEST_LIST.getText())) {
                        printListOfActiveUsers(response.split(" ")[1]);
                    }

                    if (response.contains(Protokoll.INVITATION_ACCEPTED.getText()) && response.contains(Protokoll.NOT_OK.getText())) {        // öffne Chat
                        System.out.println("Deine Freundschaftsanfrage wurde abgelehnt von " + response.split(" ")[2]);
                    } else if (response.contains(Protokoll.INVITATION_ACCEPTED.getText()) && response.contains(Protokoll.IS_OK.getText())) {        // öffne Chat
                        System.out.println("Deine Freundschaftsanfrage wurde angenommen von " + response.split(" ")[2]);
                    } else if (response.contains(Protokoll.INVITATION_ACCEPTED.getText())) {        // öffne Chat
                        chatInformation = response;
                        setHasChatWithFriend();
                    }

                    if (response.contains(Protokoll.RECEIVED_INVITATION.getText())) {        // beantworte Einladung
                        //checken ob User eingeladen wurde von jmd
                        invitingUser = response.split(" ")[2];
                        setWasInvited(true);    //ändert was Invited und feuert dies an alle Listener ab
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Startet den Sending Thread
     * Zuständig für Benutzereingaben und Senden der Nachrichten
     */
    private void startSendingThread() {
        new Thread(() -> {
            try {
                addPropertyChangeListener(new MyPropertyChangeListener());      //hört auf Änderungen von wasInvited und hasChatWithFriend
                showMenue();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     * Zeigt dem User die Interaktionsoptionen an
     */
    private void showMenue() throws IOException {
        while (!getHasChatWithFriend()) {
            System.out.println("Deine Menüoptionen sind:");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (!isLoggedIn) {
                System.out.println("Zum Registrieren bitte 'r' drücken.");
                System.out.println("Zum Anmelden bitte 'a' drücken.");

                while (true) {
                    System.out.println("Bitte gib eine Option an:");
                    String line = readerFromTerminal.nextLine();
                    if (line.equals("r")) {
                        System.out.println("Du wirst zum registrieren weitergeleitet.");
                        register();     //done
                        break;
                    } else if (line.equals("a")) {
                        System.out.println("Du wirst zum anmelden weitergeleitet.");
                        login();        //done
                        break;
                    }
                }
            } else {
                System.out.println("Zum Anfordern der aktiven Benutzerliste bitte 'l' drücken.");
                System.out.println("Zum Einladungen verschicken bitte 'e' drücken.");

                while (!getHasChatWithFriend()) {
                    System.out.println("Bitte gib eine Option an:");
                    String line = readerFromTerminal.nextLine();
                    if (line.equals("l")) {
                        System.out.println("Du forderst die Liste aller aktiven Benutzer an.");
                        requestList();      //done
                        break;
                    } else if (line.equals("e")) {
                        System.out.println("Du willst eine Einladung verschicken.");
                        System.out.println("An wen soll die Einladung gehen (bitte Namen eingeben):");  //todo checken ob der user überhaupt existiert auf serverseite?
                        System.out.println("Username: ");
                        String username = readerFromTerminal.nextLine();
                        while (isEmpty(username)) {
                            System.out.print("Username: ");
                            username = readerFromTerminal.nextLine();
                            System.out.println();
                        }
                        sendInvitation(username);       //done
                        System.out.println("WARTEN AUF ANNAHME.");
                        break;
                    }
                }
            }
        }
    }

    /**
     * Registrierung
     */
    private void register() throws IOException {
        System.out.println("Zum Registrieren gib bitte einen Usernamen und ein Passwort nacheinander ein!");
        String username = getUsernameFromTerminal();
        String password = getPasswort();
        registerWithServer(username, password);
    }

    /**
     * Absetzen der Registierung an den Server
     */
    private void registerWithServer(String username, String password) {
        String request = Protokoll.REGISTER + " " + username + " " + password;
        sendResponse(request);
    }

    /**
     * Einloggen und absenden der Einlogdaten + public key an den Server
     */
    private void login() throws IOException {
        System.out.println("Zum Anmelden gib bitte deinen Usernamen und dein Passwort ein!");
        String username = getUsernameFromTerminal();
        String password = getPasswort();
        String publicKeyAsString = KeyPairManager.publicKeyToString(ownKeyPairManager.getPublicKey());
        this.username = username;
        loginWithServer(username, password, publicKeyAsString);
    }

    /**
     * Sendet die Einlogrequest an den Server
     */
    private void loginWithServer(String username, String password, String publicKey) {
        String request =
                Protokoll.LOGIN + " " + username + " " + password + " " + this.udpPortClient + " " + this.socket.getInetAddress() + " " + publicKey;
        sendResponse(request);
    }

    /**
     * Eingaben des Usernamens in Terminal
     */
    private String getUsernameFromTerminal() {
        System.out.print("Username: ");
        String username = readerFromTerminal.nextLine();
        System.out.println();
        while (isEmpty(username)) {
            System.out.print("Username: ");
            username = readerFromTerminal.nextLine();
            System.out.println();
        }
        return username;
    }

    /**
     * Eingeben des Passworts in Terminal
     */
    private String getPasswort() {
        System.out.print("Passwort: ");
        String password = readerFromTerminal.nextLine();
        System.out.println();
        while (isEmpty(password)) {
            System.out.print("Passwort: ");
            password = readerFromTerminal.nextLine();
            System.out.println();
        }
        return password;
    }

    private boolean isEmpty(String str) {
        return str.equals("");
    }

    /**
     * Absenden der Request eine Liste zu erhalten
     */
    private void requestList() {
        String request = Protokoll.REQUEST_LIST + " " + this.username;
        sendResponse(request); // response = Liste + evtl., dass man eingeladen wurde + Einladung akzeptiert von jmd
    }

    /**
     * Fragt User, ob er mit Person Chatten möchte
     */
    private void askIfInvitationAccepted() throws IOException {
        boolean hasAcceptedInvitation = acceptInvitation(invitingUser);
        if (hasAcceptedInvitation) {
            sendResponse(Protokoll.INVITATION_ACCEPTED.getText() + " " + Protokoll.IS_OK.getText() + " " + invitingUser + " " + this.username);
        } else {
            sendResponse(Protokoll.INVITATION_ACCEPTED.getText() + " " + Protokoll.NOT_OK.getText() + " " + invitingUser + " " + this.username);
        }
    }

    /**
     * Merken der Informationen des Freundes (name, port, inetAddress, public key)
     */
    private void setNewFriendsInformation(String response) throws Exception {
        String[] infos = response.split(Protokoll.INVITATION_ACCEPTED.getText())[1].split(" ");
        this.name = infos[1];
        this.friendsPort = Integer.parseInt(infos[2]);
        this.inetAddress = infos[3];
        this.friendsPublicKey = KeyPairManager.stringToPublicKey(infos[4]);
    }

    /**
     * eingeben ob man erhaltene Einladung annimmt
     */
    private boolean acceptInvitation(String invitingUser) throws IOException {
        System.out.println("Du hast eine Anfrage bekommen zum Chatten! Sie ist von: " + invitingUser);
        System.out.println("Mit 'j' akzeptierst du die Anfrage.");
        System.out.println("Mit 'n' lehnst du ab.");
        System.out.println("Möchtest du mit " + invitingUser + " chatten? ");
        String answer = "";
        while (isEmpty(answer) || (!answer.equals("j") && !answer.equals("n"))) {
            System.out.println("Bitte gib entweder 'j' oder 'n' ein. Bestätige deine Antwort mit erneuter " +
                    "Eingabe. Deine Antwort: ");
            Scanner scan = new Scanner(System.in);
            answer = scan.nextLine();
            System.out.println();
        }
        if (answer.equals("j")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ausgeben der liste aktiver user (erhalten aus server response)
     * @param response
     */
    private void printListOfActiveUsers(String response) {
        String[] names = response.split(",");
        System.out.println("Aktive Benutzer: ");
        for (String name : names) {
            System.out.println(name);
        }
    }

    /**
     * senden der Einladung für Chat mit Freund an Server (mit eigenem + Freundesnamen)
     */
    private void sendInvitation(String usernameFriend) {
        String request = Protokoll.REQUEST_INVITATION + " " + this.username + " " + usernameFriend;     //first name: your name, second: your friends name
        sendResponse(request);
    }

    /**
     * Response an Server senden
     */
    public void sendResponse(String text) {
        try {
            System.out.println("Sende an Server: " + text);     //todo delete
            writerToServer.write(text + "\n");
            writerToServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }


    public static void main(String[] args) {
        TcpClient client = new TcpClient(args);
    }


}