package com.pitaya.terrarium.server;

import com.pitaya.terrarium.game.Terrarium;
import com.pitaya.terrarium.server.network.ServerCommunicator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class TerrariumServer {
    public final Properties properties;
    public final Terrarium terrarium;
    public final ServerCommunicator communicator;

    public TerrariumServer() {
        this.properties = new Properties();
        properties.setProperty("server-port", "25565");
        properties.setProperty("server-ip", "127.0.0.1");

        try (FileOutputStream out = new FileOutputStream("server.properties")) {
            properties.store(out, "Terrarium Server Configs");
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            communicator = new ServerCommunicator(Integer.parseInt(properties.getProperty("port")), InetAddress.getByName(properties.getProperty("ip")));
        } catch (UnknownHostException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
        this.terrarium = new Terrarium();
        runTerrarium();
        loadCommunicator();
    }

    public void runTerrarium() {
        terrarium.startWorld();
    }

    public void loadCommunicator() {
        communicator.load();
    }
}
