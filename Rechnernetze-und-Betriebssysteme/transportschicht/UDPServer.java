package exampleUDP;

import java.io.IOException;
import java.net.*;

class UDPServer {
    private static final int WINDOW_SIZE = 4;
    private static final long TIMEOUT = 1000;

    public static void main(String[] args) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData;
        long expectedId = 0;

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String receivedSentence = new String(receivePacket.getData()).trim();
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            String[] parts = receivedSentence.split("/");
            String requestType = parts[0];
            long packetId = Long.parseLong(parts[1].split("=")[1]);
            String sendedStartTime = parts[2].split("=")[1];

            if (requestType.equals(RequestType.PING.getRequest())) {
                if (packetId == expectedId) {
                    expectedId++;
                    sendData = createAck(packetId, sendedStartTime);
                    sendAck(sendData, IPAddress, port, serverSocket);
                } else if (packetId < expectedId) {
                    sendData = createAck(packetId, sendedStartTime);
                    sendAck(sendData, IPAddress, port, serverSocket);
                }
            }
        }
    }

    private static byte[] createAck(long id, String sendedStartTime) {
        String ack = RequestType.PONG.getRequest() + " ACK/id=" + id + "/time=" + sendedStartTime +"\n";
        return ack.getBytes();
    }

    private static void sendAck(byte[] sendData, InetAddress IPAddress, int port, DatagramSocket serverSocket) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);
        System.out.println("Sending ACK to Client with id: " + (new String(sendData)).trim());
    }
}

