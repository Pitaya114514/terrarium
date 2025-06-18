package com.pitaya.terrarium.game.item.weapon;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.barrage.ExplosiveBullet;
import com.pitaya.terrarium.game.item.Item;
import org.joml.Vector2f;

public class SDMGItem extends Item {
    private final Vector2f ownerPos = new Vector2f();
    private final Vector2f targetPos0 = new Vector2f();

    public SDMGItem() {
        super("S.D.M.G.", 1);
    }

    @Override
    public void useFuc(World world) {
        world.addEntity(new ExplosiveBullet(ownerPos.set(owner.position), targetPos0.set(owner.targetPos), 10.0f, Entity.EntityGroups.PLAYER));
    }
}
