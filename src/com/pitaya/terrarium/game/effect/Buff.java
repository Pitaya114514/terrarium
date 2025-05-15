package com.pitaya.terrarium.game.effect;

import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.player.PlayerEntity;

public abstract class Buff extends Effect {
    public Buff(String name, int time) {
        super(name, time);
    }

    @Override
    public void setAttached(LivingEntity player) {
        if (player instanceof PlayerEntity) {
            super.setAttached(player);
        }
    }
}
