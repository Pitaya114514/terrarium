package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class PhantasmalBolt extends BarrageEntity implements Actionable {
    private Vector2f target;
    private float slope;
    private boolean direction;
    public PhantasmalBolt(Vector2f position, Vector2f target) {
        super("Phantasmal Bolt", new Box(8, 8, 210, true), new MoveController(true), position.x, position.y);
        setTarget(target);
        slope = PosTool.getSlope(position, target);
        direction = PosTool.getDirection(position, target);
    }

    @Override
    public void action(World world) {
        PosTool.movePos(position, direction, slope, 20.0f);
    }

    @Override
    public void setTarget(Vector2f pos) {
        target = pos;
    }
}
