package com.pitaya.terrarium.game.entity.life;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.item.Backpack;
import com.pitaya.terrarium.game.item.other.*;
import org.joml.Vector2f;

public class PlayerEntity extends LivingEntity {
    private final Backpack backpack = new Backpack(this, 30);
    public final Vector2f targetPos = new Vector2f();

    public PlayerEntity(String name, Vector2f position) {
        super(name, new Box( 20, 30, 0, false), new MoveController(false), position, 100, 0, 50);
        backpack.addItem(new SlimeCrownItem());
        backpack.addItem(new SuspiciousLookingEyeItem());
    }

    public Backpack getBackpack() {
        return backpack;
    }

}
