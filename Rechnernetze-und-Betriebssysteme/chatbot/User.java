package client_server;

import java.net.Socket;
import client_server.security.KeyPairManager;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public class User {

    private String username;
    private String password;
    private int port;
    private String inetAddress;
    private Socket socket;
    private boolean hasReceivedInvitation;      // hat Einladung bekommen
    private String potentialChatPartnerName;
    private boolean hasInvitedUser;      // hat jemanden eingeladen
    private String invitingUser;
    private boolean chatAccepted;     // Einladung wurde angenommen und man sollte verbunden werden -> hat jetzt einen eigenen Chat
    private String chatPartnerName;
    private PublicKey publicKey;

    public User(String username, String password, int port, String inetAddress, Socket socket, PublicKey publicKey) {
        this.username = username;
        this.password = password;
        this.port = port;
        this.inetAddress = inetAddress;
        this.socket = socket;
        this.publicKey = publicKey;
    }

    public String getUsername() {
        return username;
    }

    public int getPort() {
        return port;
    }

    public String getInetAddress() {
        return inetAddress;
    }

    public void setHasReceivedInvitation(boolean hasReceivedInvitation) {
        this.hasReceivedInvitation = hasReceivedInvitation;
    }

    public void setPotentialChatPartnerName(String potentialChatPartnerName) {
        this.potentialChatPartnerName = potentialChatPartnerName;
    }

    public void setHasInvitedUser(boolean hasInvitedUser) {
        this.hasInvitedUser = hasInvitedUser;
    }

    public void setInvitingUser(String invitingUser) {
        this.invitingUser = invitingUser;
    }

    public Socket getSocket() {
        return socket;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
