package com.pitaya.terrarium.game;

import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.world.WorldInfo;

public interface Terrarium {

    int executeCommand(String context);

    void addEntity(Entity entity);

    void useItem(Item item);

    void sendMassage(Object sender, String message);

    int getTps();

    WorldInfo getWorldInfo();
}
