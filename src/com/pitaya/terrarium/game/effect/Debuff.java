package com.pitaya.terrarium.game.effect;

import com.pitaya.terrarium.game.entity.life.LivingEntity;

public abstract class Debuff extends Effect {
    public Debuff(String name, int time) {
        super(name, time);
    }
}
