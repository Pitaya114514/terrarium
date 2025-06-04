package com.pitaya.terrarium.game;

import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.world.WorldInfo;

public interface Terrarium {
    class WorldValidator {

    }

    void addEntity(Entity entity);

    void useItem(Item item);

    void sendMassage(String message);

    int getTps();

    WorldInfo getWorldInfo();
}
