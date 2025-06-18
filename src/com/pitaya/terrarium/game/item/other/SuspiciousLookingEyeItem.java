package com.pitaya.terrarium.game.item.other;

import com.pitaya.terrarium.game.entity.life.mob.boss.EyeOfCthulhuEntity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.util.Util;

public class SuspiciousLookingEyeItem extends Item {
    public SuspiciousLookingEyeItem() {
        super("SuspiciousLookingEye", 20);
    }

    @Override
    public void useFuc(World world) {
        world.addEntity(new EyeOfCthulhuEntity(Util.Math.getRandomPos(owner.position, 600), owner));
    }
}
