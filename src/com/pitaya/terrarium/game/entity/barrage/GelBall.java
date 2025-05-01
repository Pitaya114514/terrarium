package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class GelBall extends BarrageEntity implements Actionable {
    public GelBall(String name, Vector2f position) {
        super(name, new Box(11, 12, 40), new MoveController(false), position.x, position.y, 40);
    }

    @Override
    public void action(World world) {

    }

    @Override
    public void setTarget(Vector2f pos) {

    }
}
