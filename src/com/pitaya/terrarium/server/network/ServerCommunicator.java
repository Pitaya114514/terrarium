package com.pitaya.terrarium.server.network;

import java.io.IOException;
import java.net.*;

public class ServerCommunicator {
    private DatagramSocket socket;
    private DatagramPacket packet;
    private Thread communicatorThread;

    public ServerCommunicator(int port, InetAddress address) {
        try {
            socket = new DatagramSocket(port, address);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        packet = new DatagramPacket(new byte[1024], 1024);
    }

    public void load() {
        communicatorThread = new Thread(() -> {
            while (true) {
                try {
                    socket.receive(packet);
                    socket.send(packet);
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
