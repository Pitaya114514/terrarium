package com.pitaya.terrarium.game.item.other;

import com.pitaya.terrarium.game.entity.life.mob.boss.SlimeKingEntity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.util.Util;

public class SlimeCrownItem extends Item {
    public SlimeCrownItem() {
        super("SlimeCrown", 20);
    }

    @Override
    public void useFuc(World world) {
        world.addEntity(new SlimeKingEntity(Util.Math.getRandomPos(owner.position, 600), owner));
    }
}
