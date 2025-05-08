package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class PhantasmalDeathray extends BarrageEntity implements Actionable {
    private Vector2f target;
    private boolean isExtended;

    public PhantasmalDeathray(Vector2f position, Vector2f target) {
        super("Phantasmal Deathray", new Box(35, 35, 450, true), new MoveController(true), position.x, position.y);
        setTarget(target);
    }

    @Override
    public void action(World world) {
        if (!isExtended) {
            Vector2f pos = new Vector2f().set(position);
            PosTool.movePos(pos, PosTool.getDirection(pos, target), PosTool.getSlope(pos, target), 10);
            world.addEntity(new PhantasmalDeathray(pos, target));
            isExtended = true;
        }
    }

    @Override
    public void setTarget(Vector2f pos) {
        target = pos;
    }
}
