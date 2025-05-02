package com.pitaya.terrarium.game.item.weapon;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.game.entity.barrage.Bullet;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class MeteorStaffItem extends Item {
    public MeteorStaffItem() {
        super("MeteorStaff");
    }

    @Override
    public void use(World world) {
        super.use(world);
        Vector2f position = owner.targetPos;
        for (int i = 0; i < 100; i++) {
            Vector2f randomPos = PosTool.getRandomPos(position, 100);
            world.addEntity(new Bullet(position, randomPos, position.distance(randomPos) / 10));
        }
    }
}
