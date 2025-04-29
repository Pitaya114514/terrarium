package com.pitaya.terrarium.client;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.client.network.ClientCommunicator;
import com.pitaya.terrarium.client.network.Server;
import com.pitaya.terrarium.client.render.Renderer;
import com.pitaya.terrarium.client.window.MainWindow;
import com.pitaya.terrarium.game.Terrarium;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class TerrariumClient {
    private static final Logger LOGGER = LogManager.getLogger(TerrariumClient.class);
    public final Properties properties;
    public final Terrarium terrarium;
    public final Renderer gameRenderer;
    public Player player;
    public final MainWindow mainWindow;
    public final ClientCommunicator communicator;
    public final Set<Server> servers;

    public TerrariumClient() {
        LOGGER.info("Loading game properties");
        this.properties = new Properties();
        properties.setProperty("game-width", "1024");
        properties.setProperty("game-height", "768");
        properties.setProperty("smooth-camara", "true");

        try (FileOutputStream out = new FileOutputStream("client.properties")) {
            properties.store(out, "Terrarium Client Configs");
        } catch (IOException e) {
            System.out.println(e);
        }

        this.servers = new HashSet<>();
        this.terrarium = new Terrarium();
        this.gameRenderer = new Renderer();
        this.mainWindow = new MainWindow();
        this.communicator = new ClientCommunicator();
        mainWindow.setVisible(true);
    }

    public void runTerrarium() {
        player = new Player("Pitaya");
        terrarium.startWorld();
        terrarium.addEntity(player.entity());
    }

    public void terminateTerrarium() {
        terrarium.endWorld();
    }

    public void loadRenderer() {
        gameRenderer.load();
    }

    public void connectToServer(Server server) {
        communicator.connect(server);
    }
}
