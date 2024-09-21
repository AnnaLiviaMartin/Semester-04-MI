package client_server;

import client_server.security.KeyPairManager;

import java.io.IOException;
import java.net.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class UdpClient {
    private BlockingQueue<String> ackQueue = new LinkedBlockingQueue<>();
    private static final String ACK_PREFIX = "ACK:";
    private static final int TIMEOUT_MILLISECONDS = 3000;
    private static final int MAX_RETRIES = 3;
    private static final String RECEIVE_COMMAND = "RECEIVED";
    private static DatagramSocket datagramSocket;
    private static InetAddress address;
    private PublicKey publicKeyFriend;
    private PrivateKey ownPrivateKey;

    /**
     * Initialisiert den UDP Client --> Verbindung zwischen Client und Client
     * Startet dabei den Empfang und Sende Thread
     * @param name Chatpartner name
     * @param friendsPort Port des Chatpartners
     * @param udpPortClient Eigener Port
     * @param myUsername Eigener Benutzername
     * @param ownPrivateKey
     * @param publicKeyFriend
     */
    public UdpClient(String name, int friendsPort, int udpPortClient, String myUsername, PrivateKey ownPrivateKey,
                     PublicKey publicKeyFriend) throws UnknownHostException, SocketException, InterruptedException {
        this.ownPrivateKey = ownPrivateKey;
        this.publicKeyFriend = publicKeyFriend;
        this.address = InetAddress.getByName("localhost");
        this.datagramSocket = new DatagramSocket(udpPortClient, address);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Du schreibst jetzt mit deinem neuen Freund " + name);
        System.out.println("Der Chat ist offen.");
        System.out.println("INFO: Die erste Nachricht wird bei deinem Freund nicht ankommen. Bestätige dies mit einem" +
                " Enter.");

        //Sende Thread
        Thread sendThread = new Thread(() -> {
            try {
                while (true) {
                    String message = scanner.nextLine();

                    if (!message.isEmpty()) {
                        sendMessageWithRetry(myUsername + ": " + message, friendsPort);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //Receive Thread
        Thread receiveThread = new Thread(() -> {
            try {

                while (true) {
                    String receivedMessage = receiveMessage(datagramSocket);

                    if (receivedMessage.startsWith(ACK_PREFIX)) {
                        ackQueue.offer(receivedMessage);
                    } else if (!receivedMessage.contains(RECEIVE_COMMAND)) {
                        System.out.println(receivedMessage);
                        sendMessage(ACK_PREFIX + RECEIVE_COMMAND, friendsPort, publicKeyFriend);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        sendThread.start();
        receiveThread.start();

		/*
		Die Methoden sendThread.join() und receiveThread.join() werden verwendet, um sicherzustellen, dass die
		Hauptmethode (main-Methode) erst dann beendet wird, wenn beide Threads (sendThread und receiveThread) beendet sind.
		 */
        sendThread.join();
        receiveThread.join();
    }

    /**
     * Empfängt eine verschlüsselte und signierte Nachricht über einen DatagramSocket,
     * entschlüsselt sie und verifiziert die Signatur.
     *
     * @param datagramSocket Der DatagramSocket, über den die Nachricht empfangen wird.
     * @return Die entschlüsselte und verifizierte Nachricht.
     * @throws Exception Wenn ein Fehler beim Empfangen, Entschlüsseln oder Verifizieren auftritt.
     * @throws IllegalArgumentException Wenn das empfangene Nachrichtenformat ungültig ist.
     * @throws SecurityException Wenn die Signatur nicht verifiziert werden kann.
     */
    private String receiveMessage(DatagramSocket datagramSocket) throws Exception {
        byte[] buffer = new byte[4096];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(packet);

        String receivedData = new String(packet.getData(), 0, packet.getLength());
        String[] parts = receivedData.split("\\|");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Ungültiges Nachrichtenformat");
        }

        String encryptedMessage = parts[0];
        String encodedSignature = parts[1];

        // Entschlüsseln der Nachricht
        String decryptedMessage = KeyPairManager.decrypt(encryptedMessage, ownPrivateKey);

        // Verifizieren der Signatur
        //Base64 nötig, um es am Ende der Leitung in einen korrekten von binärem Bytecode String umzusetzen (Oder sogar
        // Bilder)
        //Bilder werden nicht über private und public verschlüsselt, sondern mit AES
        byte[] signature = Base64.getDecoder().decode(encodedSignature);
        boolean isValid = KeyPairManager.verifySignature(decryptedMessage, signature, publicKeyFriend);

        if (!isValid) {
            throw new SecurityException("Signatur konnte nicht verifiziert werden");
        }

        return decryptedMessage;
    }


    /**
     * Verschlüsselt eine Nachricht, signiert sie und sendet sie über einen DatagramSocket.
     *
     * @param message Die zu sendende Nachricht im Klartext.
     * @param udpPortFriend Der UDP-Port des Empfängers.
     * @param friendsPublicKey Der öffentliche Schlüssel des Empfängers für die Verschlüsselung.
     * @throws Exception Wenn ein Fehler beim Verschlüsseln, Signieren oder Senden auftritt.
     */
    private void sendMessage(String message, int udpPortFriend, PublicKey friendsPublicKey) throws Exception {
        // 1. Verschlüsseln der Nachricht
        String encryptedMessage = KeyPairManager.encrypt(message, friendsPublicKey);

        // 2. Signieren der ursprünglichen Nachricht
        byte[] signature = KeyPairManager.signMessage(message, ownPrivateKey);
        String encodedSignature = Base64.getEncoder().encodeToString(signature);

        // 3. Kombinieren von verschlüsselter Nachricht und Signatur
        String messageToSend = encryptedMessage + "|" + encodedSignature;

        // 4. Senden der kombinierten Nachricht
        byte[] buffer = messageToSend.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, udpPortFriend);
        datagramSocket.send(packet);
    }

    /**
     * Erneutes senden nach nicht erfolgreich gesendeter Nachricht
     */
    private void sendMessageWithRetry(String message, int port) throws Exception {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            sendMessage(message, port, publicKeyFriend);

            if (waitForAcknowledgement()) {
                return; // Erfolgreich gesendet und bestätigt
            }

            if (attempt < MAX_RETRIES - 1) {
                System.out.println("Keine Bestätigung erhalten. Erneuter Versuch " + (attempt + 2) + " von " + MAX_RETRIES);
            }
        }

        System.out.println("Nachricht konnte nach " + MAX_RETRIES + " Versuchen nicht bestätigt werden.");
    }

    /**
     * Setzen der Wartezeit für Ankunftsbestätigung
     */
    private boolean waitForAcknowledgement() {
        try {
            String ack = ackQueue.poll(TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
            return ack != null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

}
