package com.pitaya.terrarium.game.entity.life.mob;

import com.pitaya.terrarium.game.entity.Action;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.life.mob.boss.EyeOfCthulhuEntity;
import com.pitaya.terrarium.game.util.PosUtil;
import com.pitaya.terrarium.game.World;
import org.joml.Vector2f;

public class ServantOfCthulhuEntity extends MobEntity {

    public ServantOfCthulhuEntity(Vector2f position, Entity target) {
        super("Servant of Cthulhu", new Box(10, 5, 34, true), new MoveController(true), position, 15, 0, 5);
        action = new Action(this.position) {
            @Override
            public void start(World world) {
                setTargetEntity(target);
            }

            @Override
            public void act(World world) {
                if (target != null) {
                    float slope = PosUtil.getSlope(this.position, getTargetPos());
                    boolean direction = PosUtil.getDirection(this.position, getTargetPos());
                    float maxSpeed = 2.5f;
                    PosUtil.movePos(this.position, direction, slope, maxSpeed);
                } else {
                    world.removeEntity(ServantOfCthulhuEntity.this);
                }
            }

            @Override
            public void end(World world) {

            }
        };
    }

    @Override
    public boolean damage(Entity source, double value) {
        if (source instanceof EyeOfCthulhuEntity) {
            return false;
        }
        super.damage(source, value);
        return true;
    }
}
