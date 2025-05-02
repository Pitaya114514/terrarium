package com.pitaya.terrarium.game.entity.life.mob.boss;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.life.PlayerEntity;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

import java.util.concurrent.ThreadLocalRandom;

public class SlimeKingEntity extends BossEntity implements Actionable {
    public enum Action {
        DEFAULT, CHASING, TELEPORTING
    }

    public SlimeKingEntity(Vector2f pos, Vector2f target) {
        super("Slime King", new Box(200, 155, 96), new MoveController(false), pos.x, pos.y, 3570, 10, 5);
        setTarget(target);
    }

    public Action actionState = Action.DEFAULT;
    private Vector2f target;

    @Override
    public void action(World world) {
        if (target == null) {
            actionState = Action.DEFAULT;
        }
        if (position.distance(target) > 700) {
            actionState = Action.TELEPORTING;
        } else {
            actionState = Action.CHASING;
        }

        switch (actionState) {
            case CHASING -> {
                if (!this.moveController.isFloating()) {
                    this.moveController.jump(ThreadLocalRandom.current().nextFloat(60.0f, 400.0f));
                } else {
                    this.moveController.moveHorizontallyTo(target.x() - position.x() > 0, 0.5f);
                }
            }
            case TELEPORTING -> {
                moveController.teleportTo(target.x, target.y + 90);
            }
            case DEFAULT -> {
                for (Entity entity : world.entityList) {
                    if (entity instanceof PlayerEntity) {
                        setTarget(entity.position);
                        break;
                    }
                }
                if (target == null) {
                    world.removeEntity(this);
                }
            }
        }
    }

    @Override
    public void setTarget(Vector2f pos) {
        this.target = pos;
    }
}
