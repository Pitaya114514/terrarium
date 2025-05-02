package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;

public abstract class BarrageEntity extends Entity {
    public final int defaultDurability;
    protected int durability;

    public BarrageEntity(String name, Box box, MoveController moveController, float x, float y, int durability) {
        super(name, box, moveController, x, y);
        this.defaultDurability = durability;
        this.durability = this.defaultDurability;
    }

    public int getDefaultDurability() {
        return defaultDurability;
    }

    public void setDurability(int durability) {
        this.durability = Math.max(durability, 0);
    }

    public int getDurability() {
        return durability;
    }
}
