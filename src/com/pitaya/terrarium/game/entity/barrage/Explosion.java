package com.pitaya.terrarium.game.entity.barrage;

import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.entity.Action;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import org.joml.Vector2f;

public class Explosion extends BarrageEntity {
    public class ExplosionAction extends Action {

        public ExplosionAction(Entity entity) {
            super(entity);
        }

        @Override
        public void start(World world) {

        }

        @Override
        public void act(World world) {
            if (getTime() > 3) {
                world.killEntity(Explosion.this);
            }
        }

        @Override
        public void end(World world) {

        }
    }

    public Explosion(Vector2f position, float range, EntityGroups group) {
        super("Explosion", new Box(range, range, 150), new MoveController(true), position, group);
        action = new ExplosionAction(this);
    }
}
