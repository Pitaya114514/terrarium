package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.World;
import org.joml.Vector2f;

public class ExplosiveBullet extends Bullet {
    public class ExplosiveBulletAction extends Bullet.BulletAction {
        public ExplosiveBulletAction(Vector2f position, Vector2f targetPos, float speed) {
            super(position, targetPos, speed);
        }

        @Override
        public void end(World world) {
            super.end(world);
            world.addEntity(new Explosion(new Vector2f().set(position), 280));
        }
    }

    public ExplosiveBullet(Vector2f position, Vector2f targetPos, float speed) {
        super(position, targetPos, speed);
        action = new ExplosiveBulletAction(this.position, targetPos, speed);
    }
}
