package exampleUDP;

import java.io.*;
import java.net.*;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

class UDPClient {

    private static final int WINDOW_SIZE = 4;  // Beispiel Fenstergröße für Go-Back-N
    private static final int TIMEOUT = 5000;   // Timeout in Millisekunden

    public static void main(String args[]) throws Exception {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(TIMEOUT);
        InetAddress IPAddress = InetAddress.getByName("localhost");

        byte[] sendData;
        long id = 0;
        long base = 0;
        Map<Long, byte[]> packetBuffer = new HashMap<>();
        Timer timer = new Timer();

        while (true) {
            System.out.println("Bitte PING eingeben, um Request an Server zu senden.");
            if (inFromUser.readLine().equals(RequestType.PING.getRequest())) {
                if (id < base + WINDOW_SIZE) {
                    LocalTime time = LocalTime.now();
                    sendData = createPing(id, time);
                    packetBuffer.put(id, sendData);
                    id++;
                }

                long finalBase = base;
                long finalId = id;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            for (long i = finalBase; i < finalBase + WINDOW_SIZE && i < finalId; i++) {
                                sendPing(packetBuffer.get(i), IPAddress, clientSocket);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, TIMEOUT, TIMEOUT);

                while (base < id) {
                    String modifiedSentence = receiveServerAnswer(clientSocket);

                    long responseId = getResponseId(modifiedSentence);
                    LocalTime time = getResponseTime(modifiedSentence);
                    long roundTripTime = time.until(LocalTime.now(), NANOS);
                    System.out.println("Server response: " + modifiedSentence);
                    System.out.println("Received package with id " + responseId);
                    System.out.println("Roundtrip time was: " + roundTripTime);

                    if (responseId >= base) {
                        base = responseId + 1;
                        timer.cancel();
                        timer = new Timer();
                        long finalBase1 = base;
                        long finalId1 = id;
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    for (long i = finalBase1; i < finalBase1 + WINDOW_SIZE && i < finalId1; i++) {
                                        sendPing(packetBuffer.get(i), IPAddress, clientSocket);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, TIMEOUT, TIMEOUT);
                    }
                }
            } else {
                System.out.println("Falsche Eingabe, bitte Versuche es erneut.");
            }
        }
    }

    private static byte[] createPing(long id, LocalTime time) {
        String sentence = RequestType.PING.getRequest() + "/id=" + id + "/zeit=" + time.toString() + "\n";
        return sentence.getBytes();
    }

    private static void sendPing(byte[] sendData, InetAddress IPAddress, DatagramSocket clientSocket) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        clientSocket.send(sendPacket);
        System.out.println("Sending data to Server with length: " + sendData.length);
    }

    private static String receiveServerAnswer(DatagramSocket clientSocket) throws IOException {
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        return new String(receivePacket.getData()).split("\n")[0];
    }

    private static long getResponseId(String modifiedSentence) {
        String[] serverResponse = modifiedSentence.split("/");
        return Long.parseLong(serverResponse[1].split("=")[1]);
    }


    private static LocalTime getResponseTime(String modifiedSentence) {
        String[] serverResponse = modifiedSentence.split("/");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss.nnnnnnnnn");
        String sendetTime = normalizeNanoseconds(serverResponse[2].split("=")[1]);
        LocalTime sendedStartTime = LocalTime.parse(sendetTime, format);
        return sendedStartTime;
    }


    private static String normalizeNanoseconds(String timeString) {
        String[] parts = timeString.split("\\.");
        if (parts.length == 2) {
            // Add trailing zeros to the nanoseconds part if it's less than 9 digits
            String nanoseconds = parts[1];
            while (nanoseconds.length() < 9) {
                nanoseconds += "0";
            }
            return parts[0] + "." + nanoseconds;
        }
        return timeString;
    }
}

