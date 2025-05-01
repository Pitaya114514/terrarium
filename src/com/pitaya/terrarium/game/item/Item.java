package com.pitaya.terrarium.game.item;

import com.pitaya.terrarium.game.entity.life.PlayerEntity;
import com.pitaya.terrarium.game.world.World;

public abstract class Item {
    public final String name;
    protected PlayerEntity owner;

    public Item(String name) {
        this.name = name;
    }

    public PlayerEntity getOwner() {
        return owner;
    }

    public void setOwner(PlayerEntity owner) {
        this.owner = owner;
    }

    public void use(World world) {
        if (owner == null) {
            return;
        }
    }
}
