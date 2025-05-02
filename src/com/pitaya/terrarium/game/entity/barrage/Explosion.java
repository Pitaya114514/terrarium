package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.tool.PosTool;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class Explosion extends BarrageEntity implements Actionable {
    public Explosion(Vector2f position) {
        super("Explosion", new Box(100, 100, 300), new MoveController(true), position.x, position.y, 300);
    }

    @Override
    public void action(World world) {
        if (time > 3) {
            world.removeEntity(this);
        }
    }

    @Override
    public void setTarget(Vector2f pos) {

    }
}
