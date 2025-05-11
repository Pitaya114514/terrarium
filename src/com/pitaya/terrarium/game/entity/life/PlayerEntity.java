package com.pitaya.terrarium.game.entity.life;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.item.Backpack;
import com.pitaya.terrarium.game.item.other.*;
import com.pitaya.terrarium.game.item.weapon.BoomstickItem;
import com.pitaya.terrarium.game.item.weapon.MeteorStaffItem;
import org.joml.Vector2f;

public class PlayerEntity extends LivingEntity {
    private final Backpack backpack = new Backpack(this, 30);
    public final Vector2f targetPos = new Vector2f();

    public PlayerEntity(String name, Vector2f position) {
        super(name, new Box( 20, 30, 0, false), new PlayerMoveController(false), position, 100, 0, 50);
        backpack.addItem(new SlimeCrownItem());
        backpack.addItem(new SuspiciousLookingEyeItem());
        backpack.addItem(new MeteorStaffItem());
        backpack.addItem(new BoomstickItem());
    }

    public Backpack getBackpack() {
        return backpack;
    }

}
