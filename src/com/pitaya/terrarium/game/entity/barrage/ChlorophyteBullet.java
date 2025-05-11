package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.util.PosUtil;
import com.pitaya.terrarium.game.World;
import org.joml.Vector2f;

public class ChlorophyteBullet extends Bullet implements Actionable {
    private Vector2f target2;
    private float speed2;

    public ChlorophyteBullet(Vector2f position, Vector2f targetPos1, Vector2f targetPos2, float speed) {
        super(position, targetPos1, speed);
        target2 = targetPos2;
        speed2 = speed;
    }

    @Override
    public void action(World world) {
        if (time < 60) {
            super.action(world);
        } else {
            speed2 += 0.015f;
            double distance = position.distance(target2);
            if (distance < speed2) {
                PosUtil.movePos(position, PosUtil.getDirection(position, target2), PosUtil.getSlope(position, target2), (float) distance);
            } else {
                PosUtil.movePos(position, PosUtil.getDirection(position, target2), PosUtil.getSlope(position, target2), speed2);
            }
            if (penetration >= 1 || time > 700) {
                world.removeEntity(this);
            }
        }
    }

    @Override
    public void setTarget(Vector2f pos) {
        super.setTarget(pos);
    }
}
