package com.pitaya.terrarium.game.entity.life.mob;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.life.mob.boss.EyeOfCthulhuEntity;
import com.pitaya.terrarium.game.util.PosUtil;
import com.pitaya.terrarium.game.World;
import org.joml.Vector2f;

public class ServantOfCthulhuEntity extends MobEntity implements Actionable {
    private Vector2f target = new Vector2f();

    public ServantOfCthulhuEntity(Vector2f position, Vector2f target) {
        super("Servant of Cthulhu", new Box(10, 5, 34, true), new MoveController(true), position, 15, 0, 5);
        setTarget(target);
    }

    @Override
    public void setTarget(Vector2f pos) {
        this.target = pos;
    }

    @Override
    public void action(World world) {
        if (target != null) {
            float slope = PosUtil.getSlope(this.position, target);
            boolean direction = PosUtil.getDirection(this.position, target);
            float maxSpeed = 2.5f;
            PosUtil.movePos(this.position, direction, slope, maxSpeed);
        } else {
            world.removeEntity(this);
        }
    }

    @Override
    public void damage(Entity source, double value) {
        if (source instanceof EyeOfCthulhuEntity) {
            return;
        }
        super.damage(source, value);
    }
}
