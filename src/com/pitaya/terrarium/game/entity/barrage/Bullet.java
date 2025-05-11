package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.util.PosUtil;
import com.pitaya.terrarium.game.World;
import org.joml.Vector2f;

public class Bullet extends BarrageEntity implements Actionable {
    protected float speed;
    private float slope;
    private boolean direction;
    private Vector2f target;
    public Bullet(Vector2f position, Vector2f targetPos, float speed) {
        super("Bullet", new Box(3, 3, 21, false), new MoveController(true), position);
        this.speed = speed;
        setTarget(targetPos);
        direction = PosUtil.getDirection(position, this.target);
        slope = PosUtil.getSlope(position, this.target);
    }

    @Override
    public void action(World world) {
        if (penetration > 1) {
            world.removeEntity(this);
        }
        PosUtil.movePos(position, direction, slope, speed);
    }

    @Override
    public void setTarget(Vector2f pos) {
        target = pos;
    }
}
