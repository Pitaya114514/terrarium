package com.pitaya.terrarium.game.entity.life.mob;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.life.mob.boss.SkeletronEntity;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class SkeletronHandEntity extends MobEntity implements Actionable {
    public enum Action {
        DEFAULT, HANGING, ATTACKING
    }

    private Action actionState = Action.DEFAULT;
    private final SkeletronEntity host;
    private int hangingCd;
    private int attackCd;
    private boolean direction;

    public SkeletronHandEntity(Vector2f pos, SkeletronEntity host, boolean direction) {
        super("Skeletron's Hand", new Box(30, 30, 60), new MoveController(true), pos.x, pos.y, 1000, 10, 5);
        this.host = host;
        this.direction = direction;
    }

    @Override
    public void action(World world) {
        if (host.getHealth() <= 0) {
            world.removeEntity(this);
            return;
        }
        hangingCd++;
        if (hangingCd < 600) {
            actionState = Action.HANGING;
        } else {
            actionState = Action.ATTACKING;
            attackCd++;
            if (attackCd > 600) {
                hangingCd = 0;
                attackCd = 0;
            }
        }

        switch (actionState) {
            case HANGING -> {
                float x = direction ? host.position.x() + 100 : host.position.x() - 100;
                position.set(x, host.position.y() - 30);
            }
            case ATTACKING -> {

            }
        }
    }

    @Override
    public void setTarget(Vector2f pos) {

    }
}
