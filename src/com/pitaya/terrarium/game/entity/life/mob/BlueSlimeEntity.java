package com.pitaya.terrarium.game.entity.life.mob;

import com.pitaya.terrarium.game.entity.Box;
import org.joml.Vector2f;

public class BlueSlimeEntity extends SlimeEntity {
    public BlueSlimeEntity(Vector2f position) {
        super("Blue Slime", new Box(40, 20, 33), position, 110, 5, 5);
    }

    public static BlueSlimeEntity summon(Vector2f position) {
        return new BlueSlimeEntity(position);
    }
}
