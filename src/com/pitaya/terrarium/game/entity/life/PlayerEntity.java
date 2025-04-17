package com.pitaya.terrarium.game.entity.life;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;

public class PlayerEntity extends LivingEntity {
    public PlayerEntity(String name, float x, float y) {
        super(name, new Box(10, 10, 0), new MoveController(false), x, y, 100, 0, 50);
    }
}
