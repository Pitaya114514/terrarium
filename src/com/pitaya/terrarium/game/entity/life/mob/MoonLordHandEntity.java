package com.pitaya.terrarium.game.entity.life.mob;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.barrage.PhantasmalBolt;
import com.pitaya.terrarium.game.entity.barrage.PhantasmalEye;
import com.pitaya.terrarium.game.entity.barrage.PhantasmalSphere;
import com.pitaya.terrarium.game.entity.life.mob.boss.MoonLordCoreEntity;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class MoonLordHandEntity extends MobEntity implements Actionable {
    enum Action {
        SHOOTING_PHANTASMAL_EYE, SHOOTING_PHANTASMAL_SPHERE, SHOOTING_PHANTASMAL_BOLT, DEFEATED;
    }
    private Action actionState;
    private MoonLordCoreEntity owner;
    private boolean direction;
    private int speCd; //130
    private int spsCd; //250
    private int spbCd; //140
    private final List<PhantasmalSphere> phantasmalSphereController = new ArrayList<>();

    private int summonPhantasmalEyeCd;
    private int summonPhantasmalSphereCd;
    private int summonPhantasmalBoltCd;
    private boolean isDefeated;

    public MoonLordHandEntity(Vector2f position, MoonLordCoreEntity owner, boolean direction) {
        super("Moon Lord", new Box(19, 19, 0, true), new MoveController(true), position.x, position.y, 86062, 50, 5);
        this.owner = owner;
        if (direction) {
            spsCd = 250;
        } else {
            spbCd = 140;
        }
        this.direction = direction;
    }

    @Override
    public void action(World world) {
        if (!owner.isAlive()) {
            world.removeEntity(this);
        }
        if (direction) {
            position.set(owner.position.x() + 290, owner.position.y() + 95);
        } else {
            position.set(owner.position.x() - 290, owner.position.y() + 95);
        }
        if (getHealth() > 1) {
            if (spsCd >= 250) {
                spbCd = 0;
                actionState = Action.SHOOTING_PHANTASMAL_EYE;
                speCd++;
            }
            if (speCd >= 130) {
                spsCd = 0;
                actionState = Action.SHOOTING_PHANTASMAL_BOLT;
                spbCd++;
            }
            if (spbCd >= 140) {
                speCd = 0;
                actionState = Action.SHOOTING_PHANTASMAL_SPHERE;
                spsCd++;
            }
        } else {
            actionState = Action.DEFEATED;
        }

        switch (actionState) {
            case SHOOTING_PHANTASMAL_EYE -> {
                summonPhantasmalEyeCd++;
                if (summonPhantasmalEyeCd >= 10) {
                    world.addEntity(new PhantasmalEye(new Vector2f().set(position), owner.getTarget().position));
                    summonPhantasmalEyeCd = 0;
                }
            }
            case SHOOTING_PHANTASMAL_BOLT -> {
                summonPhantasmalBoltCd++;
                if (summonPhantasmalBoltCd >= 65) {
                    world.addEntity(new PhantasmalBolt(new Vector2f().set(position), owner.getTarget().position));
                    summonPhantasmalBoltCd = 0;
                }
            }
            case SHOOTING_PHANTASMAL_SPHERE -> {
                summonPhantasmalSphereCd++;
                if (summonPhantasmalSphereCd >= 23) {
                    PhantasmalSphere sphere = new PhantasmalSphere(new Vector2f().set(position), owner.getTarget().position);
                    phantasmalSphereController.add(sphere);
                    world.addEntity(sphere);
                    summonPhantasmalSphereCd = 0;
                }
                if (spsCd == 249) {
                    for (PhantasmalSphere sphere : phantasmalSphereController) {
                        sphere.move();
                    }
                }
            }
            case DEFEATED -> {
                if (!isDefeated) {
                    world.addEntity(new TrueEyeOfCthulhuEntity(position, owner));
                    box.damage = 80;
                    healthManager.isInvincible = true;
                    isDefeated = true;
                }
            }
        }
    }

    @Override
    public void damage(Entity source, double value) {
        if (getHealth() - value <= 0) {
            setHealth(1);
        } else {
            super.damage(source, value);
        }
    }
}
