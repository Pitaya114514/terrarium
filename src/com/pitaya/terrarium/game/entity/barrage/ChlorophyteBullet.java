package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.mob.MobEntity;
import com.pitaya.terrarium.game.util.PosUtil;
import org.joml.Vector2f;

public class ChlorophyteBullet extends Bullet {
    public class ChlorophyteBulletAction extends BulletAction {
        private Entity targetEntity;
        private int cd;

        public ChlorophyteBulletAction(Vector2f position, Vector2f targetPos, float speed) {
            super(position, targetPos, speed);
        }

        @Override
        public void start(World world) {
            super.start(world);
            findTarget(world);
        }

        @Override
        public void act(World world) {
            if (penetration > 0) {
                world.removeEntity(ChlorophyteBullet.this);
            }
            cd++;
            if (cd > 23 && targetEntity != null && targetEntity.isAlive()) {
                PosUtil.movePos(position, PosUtil.getDirection(position, targetEntity.position), PosUtil.getSlope(position, targetEntity.position), speed);
            } else {
                super.act(world);
            }
        }

        private void findTarget(World world) {
            for (Entity entity : world.entityList) {
                if (entity instanceof MobEntity) {
                    targetEntity = entity;
                    return;
                }
            }
        }
    }

    public ChlorophyteBullet(Vector2f position, Vector2f targetPos, float speed) {
        super(position, targetPos, speed);
        action = new ChlorophyteBulletAction(this.position, targetPos, speed);
    }
}
