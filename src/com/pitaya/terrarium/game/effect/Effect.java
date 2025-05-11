package com.pitaya.terrarium.game.effect;

import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.entity.life.LivingEntity;

public abstract class Effect {
    public final String name;
    protected LivingEntity attached;
    public final int totalTime;
    private int time;

    public Effect(String name, int time) {
        this.name = name;
        this.totalTime = time;
        this.time = totalTime;
    }

    public void effect(World world) {
        if (time <= 0) {
            attached.getEffectSet().remove(this);
        }
        time--;
    }

    public LivingEntity getAttached() {
        return attached;
    }

    public void setAttached(LivingEntity attached) {
        this.attached = attached;
    }
}
