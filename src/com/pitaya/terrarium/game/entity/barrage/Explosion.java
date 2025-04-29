package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import org.joml.Vector2f;

public class Explosion extends BarrageEntity {
    public Explosion(String name, Vector2f position) {
        super(name, new Box(100, 100, 300), new MoveController(true), position.x, position.y, 300);
    }
}
