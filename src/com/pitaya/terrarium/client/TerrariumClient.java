package com.pitaya.terrarium.client;

import com.pitaya.terrarium.client.network.ClientCommunicator;
import com.pitaya.terrarium.client.network.RemoteTerrarium;
import com.pitaya.terrarium.client.network.Server;
import com.pitaya.terrarium.client.render.Renderer;
import com.pitaya.terrarium.client.window.MainWindow;
import com.pitaya.terrarium.game.Terrarium;
import com.pitaya.terrarium.game.LocalTerrarium;
import com.pitaya.terrarium.game.network.CommunicationException;
import com.pitaya.terrarium.game.world.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class TerrariumClient {
    private static final Logger LOGGER = LogManager.getLogger(TerrariumClient.class);
    public static final String CLIENT_VERSION = "b1.4.0.1-client";

    public final Properties properties;
    public final Renderer gameRenderer;
    public Player player;
    public final MainWindow mainWindow;
    public final ClientCommunicator communicator;
    public final Set<Server> servers;
    private Terrarium terrarium;

    public TerrariumClient() {
        LOGGER.info("Terrarium | {}", CLIENT_VERSION);

        if (GraphicsEnvironment.isHeadless()) {
            LOGGER.fatal("The current graphics environment does not support window display!");
            System.exit(1);
        }

        this.properties = new Properties();
        properties.setProperty("game-width", "1600");
        properties.setProperty("game-height", "900");
        properties.setProperty("maximum-renderable-entities", "500");
        properties.setProperty("smooth-camara", "false");
        properties.setProperty("debug-mode", "true");
        properties.setProperty("auto-aim", "true");

        String name = "client.properties";
        try (InputStream input = new FileInputStream(name)) {
            properties.load(input);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                LOGGER.warn("No properties! Properties will be generated");
                outputProperties(name);
            } else {
                LOGGER.error("Properties loading failed:", e);
            }
        }

        GameLoader.setPath("saves");
        this.servers = new HashSet<>();
        this.gameRenderer = new Renderer();
        this.communicator = new ClientCommunicator();
        this.mainWindow = new MainWindow();
        mainWindow.setVisible(true);
    }


    public void outputProperties(String name) {
        try (OutputStream output = new FileOutputStream(name)) {
            properties.store(output, "Terrarium Client Configs");
        } catch (IOException ex) {
            LOGGER.error("Properties generation failed: ", ex);
        }
        try (InputStream input = new FileInputStream(name)) {
            properties.load(input);
        } catch (IOException ex) {
            LOGGER.error("Properties loading failed:: ", ex);

        }
    }

    public void runLocalTerrarium(Player player, WorldInfo worldInfo) {
        this.terrarium = new LocalTerrarium();
        this.player = player;
        LOGGER.info("Loading world: {}, Player: {}", worldInfo, player);
        LocalTerrarium lTerrarium = (LocalTerrarium) this.terrarium;
        lTerrarium.importWorldInfo(worldInfo);
        lTerrarium.startWorld();
        lTerrarium.addEntity(this.player.entity());
        gameRenderer.load();
    }

    public void runRemoteTerrarium(Player player, Server server) throws CommunicationException {
        this.terrarium = new RemoteTerrarium();
        this.player = player;
        LOGGER.info("Connecting to server: {}, Player: {}", server, player);
        communicator.connect(server, this.player);
        gameRenderer.load();
    }

    public Terrarium getTerrarium() {
        return terrarium;
    }
}
