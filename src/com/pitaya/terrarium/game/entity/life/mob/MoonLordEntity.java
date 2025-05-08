package com.pitaya.terrarium.game.entity.life.mob;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.mob.boss.MoonLordCoreEntity;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class MoonLordEntity extends MobEntity implements Actionable {
    enum Action {
        DEFEATED
    }

    private Action actionState;
    private MoonLordCoreEntity owner;
    private boolean isDefeated;

    public MoonLordEntity(Vector2f position, MoonLordCoreEntity owner) {
        super("Moon Lord", new Box(19, 19, 0, true), new MoveController(true), position.x, position.y, 86062, 50, 5);
        this.owner = owner;
    }

    @Override
    public void action(World world) {
        if (!owner.isAlive()) {
            world.removeEntity(this);
        }
        position.set(owner.position.x(), owner.position.y() + 340);
        if (getHealth() > 1) {

        } else {
            actionState = Action.DEFEATED;
        }
        switch (actionState) {
            case DEFEATED -> {
                if (!isDefeated) {
                    world.addEntity(new TrueEyeOfCthulhuEntity(position, owner));
                    box.damage = 80;
                    healthManager.isInvincible = true;
                    isDefeated = true;
                }
            }
            case null -> {

            }
        }
    }

    @Override
    public void damage(Entity source, double value) {
        if (getHealth() - value <= 0) {
            setHealth(1);
        } else {
            super.damage(source, value);
        }
    }
}
