package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.entity.Action;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.util.PosUtil;
import com.pitaya.terrarium.game.World;
import org.joml.Vector2f;

public class Bullet extends BarrageEntity {
    public class BulletAction extends Action {
        protected float speed;
        protected float slope;
        private boolean direction;

        public BulletAction(Vector2f position, Vector2f targetPos, float speed) {
            super(position);
            this.speed = speed;
            direction = PosUtil.getDirection(position, targetPos);
            slope = PosUtil.getSlope(position, targetPos);
        }

        @Override
        public void start(World world) {

        }

        @Override
        public void act(World world) {
            if (penetration > 1) {
                world.removeEntity(Bullet.this);
            }
            PosUtil.movePos(position, direction, slope, speed);
        }

        @Override
        public void end(World world) {

        }
    }

    public Bullet(Vector2f position, Vector2f targetPos, float speed) {
        super("Bullet", new Box(3, 3, 21, false), new MoveController(true), position);
        action = new BulletAction(this.position, targetPos, speed);
    }
}
