package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class Shell extends BarrageEntity implements Actionable {
    private Vector2f pos;
    private float slope;
    private int quadrant;
    public Shell(Vector2f position, Vector2f targetPos) {
        super("shell", new Box(30, 30, 50), new MoveController(true), position.x, position.y, 53);
        setTarget(targetPos);
        slope = PosTool.getSlope(position, pos);
        quadrant = PosTool.getQuadrant(position, pos);
    }

    @Override
    public void setTarget(Vector2f pos) {
        this.pos = pos;
    }


    @Override
    public void action(World world) {
        if (pos != null) {
            PosTool.movePos(position, quadrant, slope, 20);
        }
    }
}
