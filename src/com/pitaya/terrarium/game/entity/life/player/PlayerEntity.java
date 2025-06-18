package com.pitaya.terrarium.game.entity.life.player;

import com.pitaya.terrarium.game.effect.Debuff;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.mob.BlueSlimeEntity;
import com.pitaya.terrarium.game.item.Backpack;
import com.pitaya.terrarium.game.item.other.*;
import com.pitaya.terrarium.game.item.weapon.SDMGItem;
import org.joml.Vector2f;

import java.util.concurrent.ThreadLocalRandom;

public class PlayerEntity extends LivingEntity {
    public static PlayerEntity summon(Vector2f position) {
        return new PlayerEntity("aPlayer" + ThreadLocalRandom.current().nextInt(), position, PlayerDifficulty.CLASSIC);
    }

    private PlayerDifficulty difficulty;
    private int usingCd;
    private final Backpack backpack = new Backpack(this, 30);
    public final Vector2f targetPos = new Vector2f();

    public PlayerEntity(String name, Vector2f position, PlayerDifficulty difficulty) {
        super(name, new Box( 20, 30, 0), new PlayerMoveController(), position, 400, 0, 50, EntityGroups.PLAYER);
        this.difficulty = difficulty == null ? PlayerDifficulty.CLASSIC : difficulty;
        backpack.addItem(new SlimeCrownItem());
        backpack.addItem(new SuspiciousLookingEyeItem());
        backpack.addItem(new SDMGItem());
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

    public int getUsingCd() {
        return usingCd;
    }

    public void reduceUsingCd() {
        if (usingCd > 0) {
            usingCd--;
        }
    }

    public void setUsingCd(int usingCd) {
        if (usingCd >= 0) {
            this.usingCd = usingCd;
        }
    }
}
