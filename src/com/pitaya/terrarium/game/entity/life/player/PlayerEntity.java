package com.pitaya.terrarium.game.entity.life.player;

import com.pitaya.terrarium.game.effect.Debuff;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.item.Backpack;
import com.pitaya.terrarium.game.item.other.*;
import com.pitaya.terrarium.game.item.weapon.BoomstickItem;
import com.pitaya.terrarium.game.item.weapon.MeteorStaffItem;
import org.joml.Vector2f;

public class PlayerEntity extends LivingEntity {
    private PlayerDifficulty difficulty;
    private final Backpack backpack = new Backpack(this, 30);
    public final Vector2f targetPos = new Vector2f();

    public PlayerEntity(String name, Vector2f position, PlayerDifficulty difficulty) {
        super(name, new Box( 20, 30, 0, false), new PlayerMoveController(false), position, 400, 0, 50);
        this.difficulty = difficulty == null ? PlayerDifficulty.CLASSIC : difficulty;
        backpack.addItem(new SlimeCrownItem());
        backpack.addItem(new SuspiciousLookingEyeItem());
        backpack.addItem(new MeteorStaffItem());
        backpack.addItem(new BoomstickItem());
    }

    public Backpack getBackpack() {
        return backpack;
    }


    public void respawn() {
        if (this.getHealth() <= 0) {
            setHealth(Math.min(defaultHealth / 2, 100));
            position.set(0, 100);
            getEffectSet().removeIf(effect -> effect instanceof Debuff);
        }
    }

    public PlayerDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(PlayerDifficulty difficulty) {
        this.difficulty = difficulty;
    }
}
