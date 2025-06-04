package com.pitaya.terrarium.game;

import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.world.IDLoader;
import com.pitaya.terrarium.game.world.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocalTerrarium implements Terrarium {
    public static final Logger LOGGER = LogManager.getLogger(LocalTerrarium.class);
    public final IDLoader idLoader;
    private World mainWorld;

    public LocalTerrarium() {
        idLoader = IDLoader.load();
    }

    public void startWorld() {
        if (mainWorld == null) {
            mainWorld = new World("MainWorld", 10, null);
        }
        Thread worldThread = new Thread(mainWorld);
        worldThread.setName(String.format("World-%s", mainWorld.hashCode()));
        worldThread.start();
    }

    public void terminateWorld() {
        mainWorld.stop();
    }

    @Override
    public void addEntity(Entity entity) {
        mainWorld.addEntity(entity);
    }

    @Override
    public void useItem(Item item) {
        mainWorld.addDisposableTickEventListener(event -> {
            if (item != null) {
                item.use(mainWorld);
            }
        });
    }

    public void killEntity(Entity entity) {
        mainWorld.killEntity(entity);
    }

    @Override
    public void sendMassage(String message) {
        mainWorld.chatroom.sendMessage(message);
    }

    @Override
    public int getTps() {
        return mainWorld.getTps();
    }

    @Override
    public WorldInfo getWorldInfo() {
        return mainWorld.getInfo();
    }

    public void importWorldInfo(WorldInfo worldInfo)  {
        mainWorld = new World(worldInfo.getName(), worldInfo.getGravity(), worldInfo.getDifficulty());
        if (worldInfo.getEntityList() != null) {
            for (Entity entity : worldInfo.getEntityList()) {
                mainWorld.addEntity(entity);
            }
        }
    }

    public String getWorldName() {
        return mainWorld.name;
    }
}
