package com.pitaya.terrarium.game.entity;

import com.pitaya.terrarium.game.World;
import org.joml.Vector2f;

public interface Actionable {

    void action(World world);

    default void setTarget(Vector2f pos) {

    }

    default void setEntity(Entity target) {

    }
}
