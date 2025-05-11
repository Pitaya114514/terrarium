package com.pitaya.terrarium.game.item.weapon;

import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.entity.barrage.Bullet;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.util.PosUtil;
import org.joml.Vector2f;

public class BoomstickItem extends Item {
    public BoomstickItem() {
        super("Boomstick");
    }

    @Override
    public void use(World world) {
        super.use(world);
        float distance = owner.targetPos.distance(owner.position) / 8.0f;
        world.addEntity(new Bullet(new Vector2f(owner.position), PosUtil.getRandomPos(owner.targetPos, distance), 6.1f));
        world.addEntity(new Bullet(new Vector2f(owner.position), PosUtil.getRandomPos(owner.targetPos, distance), 6.6f));
        world.addEntity(new Bullet(new Vector2f(owner.position), PosUtil.getRandomPos(owner.targetPos, distance), 7.8f));
        world.addEntity(new Bullet(new Vector2f(owner.position), PosUtil.getRandomPos(owner.targetPos, distance), 9.1f));
    }
}
