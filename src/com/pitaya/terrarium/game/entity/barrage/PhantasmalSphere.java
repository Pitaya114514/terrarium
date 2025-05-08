package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class PhantasmalSphere extends BarrageEntity implements Actionable {
    private boolean shouldMove;
    private Vector2f target;
    private float slope;
    private boolean direction;
    private float speed;
    private boolean isPositioned;

    public PhantasmalSphere(Vector2f position, Vector2f target) {
        super("Phantasmal Sphere", new Box(40, 40, 240, true), new MoveController(true), position.x, position.y);
        setTarget(target);
    }


    @Override
    public void action(World world) {
        if (shouldMove) {
            if (!isPositioned) {
                slope = PosTool.getSlope(position, target);
                direction = PosTool.getDirection(position, target);
                speed = 5.0f;
                isPositioned = true;
            }
            PosTool.movePos(position, direction, slope, speed);
        }
    }

    @Override
    public void setTarget(Vector2f pos) {
        target = pos;
    }

    public void move() {
        shouldMove = true;
    }
}
