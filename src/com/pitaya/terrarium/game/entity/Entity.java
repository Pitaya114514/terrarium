package com.pitaya.terrarium.game.entity;

import com.pitaya.terrarium.game.entity.life.LivingEntity;
import org.joml.Vector2f;

public abstract class Entity {
    public final String name;
    public final Box box;
    public final MoveController moveController;
    public int time;
    public final Vector2f position = new Vector2f();

    public Entity(String name, Box box, MoveController moveController, float x, float y) {
        this.name = name;
        this.box = box;
        this.moveController = moveController;
        position.set(x, y);
        box.center = position;
        moveController.pos = position;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public void attack(LivingEntity attacked, double value) {
        if (this == attacked) {
            return;
        }
        if (value < 0) {
            value = 0;
        }
        attacked.damage(this, value);
    }
}
