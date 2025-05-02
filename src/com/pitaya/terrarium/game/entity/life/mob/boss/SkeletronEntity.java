package com.pitaya.terrarium.game.entity.life.mob.boss;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.life.mob.SkeletronHandEntity;
import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class SkeletronEntity extends BossEntity implements Actionable {
    public enum Action {
        DEFAULT, FLOATING, ATTACKING
    }
    private Action actionState = Action.DEFAULT;
    private SkeletronHandEntity leftHand;
    private SkeletronHandEntity rightHand;
    private int floatingCd;
    private int attackCd;
    private boolean isHandSummoned;
    private int accelerationTime;
    private float floatingSpeed;
    private boolean floatingDirection;
    private final Vector2f floatingTarget = new Vector2f();
    private Vector2f target;

    public SkeletronEntity(Vector2f pos, Vector2f target) {
        super("Skeletron", new Box(50, 50, 200), new MoveController(true), pos.x, pos.y, 14000, 50, 5);
        this.target = target;
        leftHand = new SkeletronHandEntity(new Vector2f().set(pos), this, false);
        rightHand = new SkeletronHandEntity(new Vector2f().set(pos), this, true);
    }

    @Override
    public void action(World world) {
        if (!isHandSummoned) {
            world.addEntity(leftHand);
            world.addEntity(rightHand);
            isHandSummoned = true;
        }
        floatingCd++;
        if (floatingCd > 1500) {
            actionState = Action.ATTACKING;
            attackCd++;
            if (attackCd > 700) {
                attackCd = 0;
                floatingCd = 0;
            }
        } else {
            actionState = Action.FLOATING;
        }
        switch (actionState) {
            case ATTACKING -> {
                PosTool.movePos(position, PosTool.getDirection(position, target), PosTool.getSlope(position, target), 4.3f);
            }
            case FLOATING -> {
                floatingTarget.set(target.x(), target.y() + 50);
                boolean direction = PosTool.getDirection(position, floatingTarget);
                if (floatingDirection != direction) {
                    floatingSpeed = 0.0f;
                }
                floatingSpeed += 0.03f;
                floatingDirection = direction;
                PosTool.movePos(position, direction, PosTool.getSlope(position, floatingTarget), floatingSpeed);
            }
        }
    }

    @Override
    public void setTarget(Vector2f pos) {
        this.target = pos;
    }
}
