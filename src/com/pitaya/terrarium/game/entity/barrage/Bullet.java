package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Action;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.util.PosUtil;
import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.util.Velocity;
import org.joml.Vector2f;

public class Bullet extends BarrageEntity {
    public static class BulletAction extends Action {
        protected float speed;
        protected Velocity velocity;

        public BulletAction(BarrageEntity entity, Vector2f targetPos, float speed) {
            super(entity);
            this.speed = speed;
            velocity = new Velocity(this.speed, PosUtil.getRadians(entity.position, targetPos));
        }

        @Override
        public void start(World world) {

        }

        @Override
        public void act(World world) {
            if (((BarrageEntity) entity).penetration > 1) {
                world.killEntity(entity);
            }
            PosUtil.movePos(entity.position, velocity);
        }

        @Override
        public void end(World world) {

        }
    }

    public Bullet(Vector2f position, Vector2f targetPos, float speed, EntityGroups group) {
        super("Bullet", new Box(3, 3, 120), new MoveController(true), position, group);
        action = new BulletAction(this, targetPos, speed);
    }
}
