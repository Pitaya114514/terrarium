package com.pitaya.terrarium.game.entity.life.mob.boss;

import com.pitaya.terrarium.game.entity.Action;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.entity.life.mob.BlueSlimeEntity;
import org.joml.Vector2f;

import java.util.concurrent.ThreadLocalRandom;

public class SlimeKingEntity extends BossEntity {


    public SlimeKingEntity(Vector2f position, Entity target) {
        super("Slime King", new Box(200, 155, 96), new MoveController(false), position, 3570, 10, 5);
        action = new Action(this) {
            enum Action {
                DEFAULT, TELEPORTING, CHASING
            }

            private Entity aTarget;
            private Action actionState;

            @Override
            public void start(World world) {
                position.set(0, 0);
                aTarget = target;
                world.addEntity(new BlueSlimeEntity(new Vector2f(entity.position)));

            }

            @Override
            public void act(World world) {
                if (target == null) {
                    actionState = Action.DEFAULT;
                }
                if (position.distance(aTarget.position) > 700) {
                    actionState = Action.TELEPORTING;
                } else {
                    actionState = Action.CHASING;
                }

                switch (actionState) {
                    case CHASING -> {
                        if (!SlimeKingEntity.this.moveController.isFloating()) {
                            SlimeKingEntity.this.moveController.jump(ThreadLocalRandom.current().nextFloat(60.0f, 400.0f), world.gravity);
                        } else {
                            SlimeKingEntity.this.moveController.moveHorizontallyTo(aTarget.position.x() - position.x() > 0, 0.5f);
                        }
                    }
                    case TELEPORTING -> moveController.teleportTo(aTarget.position.x(), aTarget.position.y() + 90);
                }
            }

            @Override
            public void end(World world) {
            }
        };
    }
}
