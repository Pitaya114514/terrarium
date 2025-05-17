package com.pitaya.terrarium.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.world.WorldLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class Terrarium {
    public static final Logger LOGGER = LogManager.getLogger(Terrarium.class);
    public static final String VERSION = "b1.3.2";
    public final WorldLoader worldLoader;
    private World mainWorld;

    public Terrarium() {
        LOGGER.info("Terrarium | {}", VERSION);
        worldLoader = new WorldLoader();
    }

    public void startWorld() {
        if (mainWorld == null) {
            mainWorld = new World("MainWorld", 10, null);
        }
        Thread worldThread = new Thread(mainWorld);
        worldThread.setName(String.format("World-%s", mainWorld.hashCode()));
        worldThread.start();
    }

    public void endWorld() {
        mainWorld.stop();
    }

    public void addEntity(Entity entity) {
        mainWorld.addEntity(entity);
    }

    public void useItem(Item item) {
        mainWorld.addDisposableTickEventListener(event -> {
            if (item != null) {
                item.use(mainWorld);
            }
        });
    }

    public void removeEntity(Entity entity) {
        mainWorld.removeEntity(entity);
    }

    public void sendMassage(String message) {
        mainWorld.chatroom.sendMessage(message);
    }

    public int getTps() {
        return mainWorld.getTps();
    }

    public List<Entity> getEntityList() {
        return mainWorld.entityList;
    }

    public int getWorldGravity() {
        return mainWorld.gravity;
    }

    public void importWorldData(String data)  {
        try {
            mainWorld = worldLoader.importWorld(data);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public String exportWorldData() throws JsonProcessingException {
        return worldLoader.exportWorld(mainWorld);
    }

    public String getWorldName() {
        return mainWorld.name;
    }
}
