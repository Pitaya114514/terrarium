package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.World;
import org.joml.Vector2f;

public class ExplosiveBullet extends Bullet {
    public static class ExplosiveBulletAction extends Bullet.BulletAction {
        public ExplosiveBulletAction(BarrageEntity entity, Vector2f targetPos, float speed) {
            super(entity, targetPos, speed);
        }

        @Override
        public void end(World world) {
            super.end(world);
            world.addEntity(new Explosion(new Vector2f().set(entity.position), 280, entity.getGroup()));
        }
    }

    public ExplosiveBullet(Vector2f position, Vector2f targetPos, float speed, EntityGroups group) {
        super(position, targetPos, speed, group);
        action = new ExplosiveBulletAction(this, targetPos, speed);
    }
}
