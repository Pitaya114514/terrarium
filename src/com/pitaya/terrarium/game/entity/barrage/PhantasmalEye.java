package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.world.World;
import javafx.geometry.Pos;
import org.joml.Vector2f;

public class PhantasmalEye extends BarrageEntity implements Actionable {
    private Vector2f target;
    private float speed;
    private float maxSpeed = 6;
    private boolean isFixed;
    private boolean fixedDirection;
    private float fixedSlope;

    public PhantasmalEye(Vector2f position, Vector2f target) {
        super("Phantasmal Eye", new Box(17, 15, 180, true), new MoveController(true), position.x, position.y);
        setTarget(target);
    }

    @Override
    public void action(World world) {
        if (time > 500) {
            world.removeEntity(this);
        }
        if (time > 95) {
            if (target.y - 50 < position.y) {
                moveController.setFloatable(true);
                if (position.distance(target) < 50) {
                    if (!isFixed) {
                        Vector2f fixedTarget = new Vector2f().set(target);
                        fixedDirection = PosTool.getDirection(position, fixedTarget);
                        fixedSlope = PosTool.getSlope(position, fixedTarget);
                        isFixed = true;
                    }
                    PosTool.movePos(position, fixedDirection, fixedSlope, speed);
                } else {
                    if (isFixed) {
                        PosTool.movePos(position, fixedDirection, fixedSlope, speed);
                    } else {
                        PosTool.movePos(position, PosTool.getDirection(position, target), PosTool.getSlope(position, target), speed);
                    }
                }
                if (speed < maxSpeed) {
                    speed += 0.18f;
                }
            } else {
                moveController.setFloatable(false);
            }
        } else {
            Vector2f randomPos = PosTool.getRandomPos(position, 1000);
            PosTool.movePos(position, PosTool.getDirection(position, randomPos), PosTool.getSlope(position, randomPos), 4.0f);
        }
    }

    @Override
    public void setTarget(Vector2f pos) {
        this.target = pos;
    }
}
