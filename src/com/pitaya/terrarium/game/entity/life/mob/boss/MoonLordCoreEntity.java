package com.pitaya.terrarium.game.entity.life.mob.boss;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.mob.MoonLordEntity;
import com.pitaya.terrarium.game.entity.life.mob.MoonLordHandEntity;
import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class MoonLordCoreEntity extends BossEntity implements Actionable {
    private LivingEntity target;
    private MoonLordHandEntity leftHand;
    private MoonLordHandEntity rightHand;
    private MoonLordEntity head;

    public MoonLordCoreEntity(Vector2f position, LivingEntity target) {
        super("Moon Lord's Core", new Box(16, 16, 0, true), new MoveController(true), position.x(), position.y(), 95625, 70, 5);
        setEntity(target);
        healthManager.isInvincible = true;
    }

    @Override
    public void action(World world) {
        if (leftHand == null) {
            leftHand = new MoonLordHandEntity(new Vector2f().set(position), this, false);
            world.addEntity(leftHand);
        }
        if (rightHand == null) {
            rightHand = new MoonLordHandEntity(new Vector2f().set(position), this, true);
            world.addEntity(rightHand);
        }
        if (head == null) {
            head = new MoonLordEntity(new Vector2f().set(position), this);
            world.addEntity(head);
        }
        if ((leftHand.getHealth() <= 1) && (rightHand.getHealth() <= 1) && (head.getHealth() <= 1)) {
            healthManager.isInvincible = false;
        }
        PosTool.movePos(position, PosTool.getDirection(position, target.position), PosTool.getSlope(position, target.position), 1.65f);
    }

    @Override
    public void setEntity(Entity target) {
        if (target instanceof LivingEntity livingEntity) {
            this.target = livingEntity;
        }
    }

    public LivingEntity getTarget() {
        return target;
    }
}
