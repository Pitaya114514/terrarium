package com.pitaya.terrarium.game;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;

public class Terrarium {
    public static final Logger LOGGER = LogManager.getLogger(Terrarium.class);
    public static final String VERSION = "b1.1";
    private World mainWorld;

    public Terrarium() {
        LOGGER.info("Terrarium | {}", VERSION);
    }

    public void startWorld() {
        mainWorld = new World(10);
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

    public void removeEntity(Entity entity) {
        mainWorld.removeEntity(entity);
    }

    public void sendMassage(String message) {
        mainWorld.chatroom.sendMessage(message);
    }

    public int getTps() {
        return mainWorld.getTps();
    }

    public List<Entity> getEntitySet() {
        return mainWorld.entityList;
    }

    public int getWorldGravity() {
        return mainWorld.gravity;
    }
}
