package com.pitaya.terrarium.game.item.other;

import com.pitaya.terrarium.game.entity.life.mob.boss.EyeOfCthulhuEntity;
import com.pitaya.terrarium.game.entity.life.mob.boss.SlimeKingEntity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class SlimeCrownItem extends Item {
    public SlimeCrownItem() {
        super("SlimeCrown");
    }

    @Override
    public void use(World world) {
        super.use(world);
        world.addEntity(new SlimeKingEntity(new Vector2f().set(owner.position.x() + 100, owner.position.y() + 40), owner.position));
    }
}
