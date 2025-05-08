package com.pitaya.terrarium.game.item.other;

import com.pitaya.terrarium.game.entity.life.mob.boss.MoonLordCoreEntity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.world.World;

public class CelestialSigilItem extends Item {
    public CelestialSigilItem() {
        super("CelestialSigil");
    }

    @Override
    public void use(World world) {
        super.use(world);
        world.addEntity(new MoonLordCoreEntity(PosTool.getRandomPos(owner.position, 600), owner));
    }
}
