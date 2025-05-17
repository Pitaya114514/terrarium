package com.pitaya.terrarium.game.item.other;

import com.pitaya.terrarium.game.entity.life.mob.boss.EyeOfCthulhuEntity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.util.PosUtil;
import com.pitaya.terrarium.game.World;

public class SuspiciousLookingEyeItem extends Item {
    public SuspiciousLookingEyeItem() {
        super("SuspiciousLookingEye");
    }

    @Override
    public void useFuc(World world) {
        world.addEntity(new EyeOfCthulhuEntity(PosUtil.getRandomPos(owner.position, 600), owner));
    }
}
