package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;

public abstract class BarrageEntity extends Entity {
    public int penetration;

    public BarrageEntity(String name, Box box, MoveController moveController, float x, float y) {
        super(name, box, moveController, x, y);
    }
}
