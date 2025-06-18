package com.pitaya.terrarium.game.entity.life.mob;

import com.pitaya.terrarium.game.entity.Action;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.util.Util;
import com.pitaya.terrarium.game.util.Velocity;
import org.joml.Vector2f;

public class ServantOfCthulhuEntity extends MobEntity {

    public static ServantOfCthulhuEntity summon(Vector2f position) {
        return new ServantOfCthulhuEntity(position, null);
    }

    public ServantOfCthulhuEntity(Vector2f position, Entity target) {
        super("Servant of Cthulhu", new Box(10, 5, 34), new MoveController(true), position, 15, 0, 5);
        action = new Action(this) {
            private Velocity velocity = new Velocity(2.5f);

            @Override
            public void start(World world) {
                setTargetEntity(target);
            }

            @Override
            public void act(World world) {
                if (target != null) {
                    velocity.radians = Util.Math.getRadians(position, getTargetPos());
                    Util.Math.movePos(entity.position, velocity);
                } else {
                    world.killEntity(ServantOfCthulhuEntity.this);
                }
            }

            @Override
            public void end(World world) {

            }
        };
    }
}
