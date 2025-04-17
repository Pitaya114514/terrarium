package com.pitaya.terrarium.game;

import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.world.World;

import java.util.HashSet;

public class Terrarium {
    private World mainWorld;

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

    public HashSet<Entity> getEntitySet() {
        return mainWorld.syncEntitySet;
    }

    public int getWorldGravity() {
        return mainWorld.gravity;
    }
}
