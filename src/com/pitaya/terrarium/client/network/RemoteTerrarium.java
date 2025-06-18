package com.pitaya.terrarium.client.network;

import com.pitaya.terrarium.game.Terrarium;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.world.WorldInfo;

import java.util.ArrayList;

public class RemoteTerrarium implements Terrarium {
    @Override
    public int executeCommand(String context) {
        return 0;
    }

    @Override
    public void addEntity(Entity entity) {

    }

    @Override
    public void useItem(Item item) {

    }

    @Override
    public void sendMassage(Object sender, String message) {

    }

    @Override
    public int getTps() {
        return 0;
    }

    @Override
    public WorldInfo getWorldInfo() {
        WorldInfo worldInfo = new WorldInfo();
        worldInfo.setEntityList(new ArrayList<>());
        worldInfo.setName("a");
        return worldInfo;
    }
}
