package com.pitaya.terrarium.game.entity.life.mob;

import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.entity.Action;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import org.joml.Vector2f;

public abstract class SlimeEntity extends MobEntity {
    public static class SlimeAction extends Action {
        private int jumpCount;
        private int jumpCd;

        public SlimeAction(SlimeEntity entity) {
            super(entity);
        }

        @Override
        public void start(World world) {

        }

        @Override
        public void act(World world) {
            if (!entity.moveController.isFloating()) {
                entity.moveController.jump(83, world.gravity);
            }
        }

        @Override
        public void end(World world) {

        }
    }

    public SlimeEntity(String name, Box box, Vector2f position, double defaultHealth, double defense, int invincibilityFrame) {
        super(name, box, new MoveController(false), position, defaultHealth, defense, invincibilityFrame);
        action = new SlimeAction(this);
    }
}
