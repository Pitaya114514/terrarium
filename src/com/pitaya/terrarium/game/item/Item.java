package com.pitaya.terrarium.game.item;

import com.pitaya.terrarium.game.entity.ItemEntity;
import com.pitaya.terrarium.game.entity.life.player.PlayerEntity;
import com.pitaya.terrarium.game.World;
import org.joml.Vector2f;

public abstract class Item {
    public final String name;
    protected ItemEntity entity;
    protected PlayerEntity owner;
    protected int usingTime;

    public Item(String name, int usingTime) {
        this.name = name;
        this.usingTime = usingTime > 0 ? usingTime : 1;
    }

    public void setOwner(PlayerEntity owner) {
        this.owner = owner;
    }

    protected abstract void useFuc(World world);

    public void use(World world) {
        if (owner == null || owner.getUsingCd() > 0) {
            return;
        }
        useFuc(world);
        owner.setUsingCd(usingTime);
    }

    public void thrown(World world) {
        if (owner != null) {
            entity = new ItemEntity(this, new Vector2f(owner.position));
        }
    }
}
