package com.pitaya.terrarium.client.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ClientCommunicator {
    private final DatagramSocket socket;
    private DatagramPacket packet;
    private Thread communicatorThread;

    public ClientCommunicator() {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        packet = new DatagramPacket(new byte[1024], 1024);
    }

    public void connect(Server server) {
        communicatorThread = new Thread(() -> {
            try {
                socket.setSoTimeout(9500);
                packet.setAddress(server.address());
                packet.setPort(server.port());
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                try {
                    socket.send(packet);
                    socket.receive(packet);
                    System.out.println(packet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        communicatorThread.setName("CommunicatorThread");
        communicatorThread.start();
    }
}
