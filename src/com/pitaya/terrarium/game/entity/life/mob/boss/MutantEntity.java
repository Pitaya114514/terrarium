package com.pitaya.terrarium.game.entity.life.mob.boss;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public class MutantEntity extends BossEntity implements Actionable {


    public MutantEntity(String name, Box box, MoveController moveController, float x, float y, double defaultHealth, double defense, int invincibilityFrame) {
        super(name, box, moveController, x, y, defaultHealth, defense, invincibilityFrame);
    }

    @Override
    public void action(World world) {

    }

    @Override
    public void setTarget(Vector2f pos) {

    }
}
