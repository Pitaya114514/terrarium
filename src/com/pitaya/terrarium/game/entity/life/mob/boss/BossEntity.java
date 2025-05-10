package com.pitaya.terrarium.game.entity.life.mob.boss;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.life.mob.MobEntity;
import org.joml.Vector2f;

public abstract class BossEntity extends MobEntity {
    public BossEntity(String name, Box box, MoveController moveController, Vector2f position, double defaultHealth, double defense, int invincibilityFrame) {
        super(name, box, moveController, position, defaultHealth, defense, invincibilityFrame);
    }
}
