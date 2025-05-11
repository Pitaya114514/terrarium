package com.pitaya.terrarium.game.item.weapon;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.game.entity.barrage.Bullet;
import com.pitaya.terrarium.game.entity.barrage.ChlorophyteBullet;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.util.PosUtil;
import com.pitaya.terrarium.game.World;
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
            Vector2f randomPos = PosUtil.getRandomPos(position, 100);
            world.addEntity(new ChlorophyteBullet(position, randomPos, owner.targetPos, position.distance(randomPos) / 10));
        }
    }
}