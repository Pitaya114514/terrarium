package org.terrarium.core.game.entity;

import org.joml.Vector2f;

import java.util.List;

public abstract class Box {
    public final Vector2f size;

    public Box(Vector2f size) {
        this.size = size == null ? new Vector2f(1, 1) : size;
    }

    public Box(float width, float height) {
        if (width <= 0) width = 1;
        if (height <= 0) height = 1;
        this.size = new Vector2f(width, height);
    }

    public abstract void collide(List<Entity> entities);
}
