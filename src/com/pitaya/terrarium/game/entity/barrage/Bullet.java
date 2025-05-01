package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class Bullet extends BarrageEntity implements Actionable {
    private float speed;
    private float slope;
    private boolean direction;
    private Vector2f target;
    public Bullet(Vector2f position, Vector2f targetPos, float speed) {
        super("Bullet", new Box(3, 3, 424), new MoveController(true), position.x, position.y, 3);
        this.speed = speed;
        setTarget(targetPos);
        direction = PosTool.getDirection(position, this.target);
        slope = PosTool.getSlope(position, this.target);
    }

    @Override
    public void action(World world) {
        PosTool.movePos(position, direction, slope, speed);
    }

    @Override
    public void setTarget(Vector2f pos) {
        target = pos;
    }
}
