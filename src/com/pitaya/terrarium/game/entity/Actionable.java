package com.pitaya.terrarium.game.entity;

import com.pitaya.terrarium.game.world.World;
import org.joml.Vector2f;

public interface Actionable {
    void action(World world);
    void setTarget(Vector2f pos);
}
