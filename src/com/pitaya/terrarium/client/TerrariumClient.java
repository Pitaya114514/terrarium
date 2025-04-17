package com.pitaya.terrarium.client;

import com.pitaya.terrarium.client.render.Renderer;
import com.pitaya.terrarium.game.Terrarium;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TerrariumClient {
    public final Properties properties;
    public final Terrarium terrarium;
    public final Renderer gameRenderer;
    public Player player;
    private Thread renderThread;
    public final MainWindow mainWindow;

    public TerrariumClient() {
        this.properties = new Properties();
        properties.setProperty("game-width", "800");
        properties.setProperty("game-height", "600");

        try (FileOutputStream out = new FileOutputStream("client.properties")) {
            properties.store(out, "Terrarium Client Configs");
        } catch (IOException e) {
            System.out.println(e);
        }

        this.terrarium = new Terrarium();
        this.gameRenderer = new Renderer();
        this.mainWindow = new MainWindow();
        mainWindow.setVisible(true);
    }

    public void runTerrarium() {
        player = new Player(terrarium, "Pitaya");
        terrarium.startWorld();
        terrarium.addEntity(player.entity());
    }

    public void terminateTerrarium() {
        terrarium.endWorld();
    }

    public void loadRenderer() {
        if (renderThread == null || renderThread.getState() == Thread.State.TERMINATED) {
            renderThread = new Thread(gameRenderer);
            renderThread.setName("RenderThread");
            renderThread.setDaemon(true);
            renderThread.start();
        } else if (renderThread.getState() == Thread.State.NEW) {
            renderThread.start();
        }
    }
}
