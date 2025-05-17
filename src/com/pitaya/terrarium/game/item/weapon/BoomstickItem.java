package com.pitaya.terrarium.game.item.weapon;

import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.entity.barrage.ChlorophyteBullet;
import com.pitaya.terrarium.game.entity.barrage.ExplosiveBullet;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.util.PosUtil;
import org.joml.Vector2f;

public class BoomstickItem extends Item {
    public BoomstickItem() {
        super("Boomstick");
    }

    @Override
    public void useFuc(World world) {
        float distance = owner.targetPos.distance(owner.position) / 8.0f;
        for (int i = 0; i < 30; i++) {
            world.addEntity(new ExplosiveBullet(new Vector2f(owner.position), PosUtil.getRandomPos(owner.targetPos, distance), 3f + i / 3f));
        }
    }
}
