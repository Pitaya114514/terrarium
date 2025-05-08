package com.pitaya.terrarium.game.entity.life.mob;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.mob.boss.MoonLordCoreEntity;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class TrueEyeOfCthulhuEntity extends MobEntity implements Actionable {
    enum Action {

    }

    private MoonLordCoreEntity master;

    public TrueEyeOfCthulhuEntity(Vector2f position, MoonLordCoreEntity master) {
        super("True Eye of Cthulhu", new Box(97, 97, 180, true), new MoveController(true), position.x, position.y, 300, 0, 0);
        healthManager.isInvincible = true;
        this.master = master;
    }

    @Override
    public void action(World world) {
        if (!master.isAlive()) {
            world.removeEntity(this);
        }
    }
}
