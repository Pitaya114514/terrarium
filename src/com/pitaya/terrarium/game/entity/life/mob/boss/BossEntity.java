package com.pitaya.terrarium.game.entity.life.mob.boss;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.life.mob.MobEntity;

public abstract class BossEntity extends MobEntity {
    public BossEntity(String name, Box box, MoveController moveController, float x, float y, double defaultHealth, double defense, int invincibilityFrame) {
        super(name, box, moveController, x, y, defaultHealth, defense, invincibilityFrame);
    }
}
