package com.pitaya.terrarium.game.item.other;

import com.pitaya.terrarium.game.entity.life.mob.boss.EyeOfCthulhuEntity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class SuspiciousLookingEyeItem extends Item {
    public SuspiciousLookingEyeItem() {
        super("SuspiciousLookingEye");
    }

    @Override
    public void use(World world) {
        super.use(world);
        world.addEntity(new EyeOfCthulhuEntity(PosTool.getRandomPos(owner.position, 600), owner.position));
    }
}
