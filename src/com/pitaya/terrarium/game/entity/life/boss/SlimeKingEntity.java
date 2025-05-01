package com.pitaya.terrarium.game.entity.life.boss;

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
        DEFAULT, CHASING
    }

    public SlimeKingEntity(float x, float y) {
        super("Slime King", new Box(200, 155, 20), new MoveController(false), x, y, 1000, 20, 5);
    }

    public Action actionState = Action.DEFAULT;
    private Vector2f target;

    @Override
    public void action(World world) {
        if (target != null) {
            actionState = Action.CHASING;
        } else {
            actionState = Action.DEFAULT;
        }
        switch (actionState) {
            case CHASING -> {
                if (!this.moveController.isFloating()) {
                    this.moveController.jump(ThreadLocalRandom.current().nextFloat(60.0f, 400.0f));
                } else {
                    this.moveController.moveHorizontallyTo(target.x() - position.x() > 0, 0.5f);
                }
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
