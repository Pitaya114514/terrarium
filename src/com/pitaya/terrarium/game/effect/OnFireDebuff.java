package com.pitaya.terrarium.game.effect;

import com.pitaya.terrarium.game.World;

public class OnFireDebuff extends Debuff {
    public OnFireDebuff(int time) {
        super("On Fire!", time);
    }

    @Override
    public void effect(World world) {
        super.effect(world);
        attached.setHealth(attached.getHealth() - 4 / 60.0f);
    }
}
