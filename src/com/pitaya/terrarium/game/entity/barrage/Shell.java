package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class Shell extends BarrageEntity implements Actionable {
    private Vector2f target;
    private float slope;
    private boolean direction;
    private float speed;
    public Shell(Vector2f position, Vector2f targetPos, float speed) {
        super("shell", new Box(7, 8, 50, false), new MoveController(false), position.x, position.y);
        this.speed = speed;
        setTarget(targetPos);
        slope = PosTool.getSlope(position, target);
        direction = PosTool.getDirection(position, target);
    }

    @Override
    public void setTarget(Vector2f pos) {
        this.target = pos;
    }


    @Override
    public void action(World world) {
        if (target != null) {
            PosTool.movePos(position, direction, slope, speed);
            if (box.isOnGround() || time > 100 || penetration > 0) {
                world.addEntity(new Explosion(new Vector2f().set(position)));
                world.removeEntity(this);
            }
        }
    }
}
