package com.pitaya.terrarium.game.entity.life.mob;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import org.joml.Vector2f;

public abstract class MobEntity extends LivingEntity {
    public MobEntity(String name, Box box, MoveController moveController, Vector2f position, double defaultHealth, double defense, int invincibilityFrame) {
        super(name, box, moveController, position, defaultHealth, defense, invincibilityFrame);
    }
}
