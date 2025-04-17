package com.pitaya.terrarium.game.entity;

import org.joml.Vector2f;

public class Box {
    public final float width;
    public final float height;
    public double damage;
    public Vector2f center;

    public Box(float width, float height, double damage) {
        this.width = width;
        this.height = height;
        this.damage = damage;
    }

    public Vector2f getTopLeft() {
        return new Vector2f(center.x() - width / 2, center.y() + height / 2);
    }

    public Vector2f getBottomLeft() {
        return new Vector2f(center.x() - width / 2, center.y() - height / 2);
    }

    public Vector2f getBottomRight() {
        return new Vector2f(center.x() + width / 2, center.y() - height / 2);
    }

    public Vector2f getTopRight() {
        return new Vector2f(center.x() + width / 2, center.y() + height / 2);
    }

    public boolean isOnGround() {
        return center.y() - height / 2 <= 0;
    }
}
