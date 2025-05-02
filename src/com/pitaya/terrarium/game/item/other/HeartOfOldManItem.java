package com.pitaya.terrarium.game.item.other;

import com.pitaya.terrarium.game.entity.life.mob.boss.EyeOfCthulhuEntity;
import com.pitaya.terrarium.game.entity.life.mob.boss.SkeletronEntity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class HeartOfOldManItem extends Item {
    public HeartOfOldManItem() {
        super("HeartOfOldMan");
    }

    @Override
    public void use(World world) {
        super.use(world);
        world.addEntity(new SkeletronEntity(PosTool.getRandomPos(owner.position, 600), owner.position));
    }
}
