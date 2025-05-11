package com.pitaya.terrarium.game.entity;

import org.joml.Vector2f;

public class Box {
    public double damage;
    public boolean isDangerous;
    public Vector2f center;
    private float width;
    private float height;

    public Box(float width, float height, double damage, boolean isDangerous) {
        setWidth(width);
        setHeight(height);
        this.damage = damage;
        this.isDangerous = isDangerous;
        this.center = new Vector2f();
    }

    public void setWidth(float width) {
        if (width > 0) {
            this.width = width;
        }
    }

    public void setHeight(float height) {
        if (height > 0) {
            this.height = height;
        }
    }

    public Vector2f getTopLeft(Vector2f vector) {
        return vector.set(center.x() - width / 2, center.y() + height / 2);
    }

    public Vector2f getBottomLeft(Vector2f vector) {
        return vector.set(center.x() - width / 2, center.y() - height / 2);
    }

    public Vector2f getBottomRight(Vector2f vector) {
        return vector.set(center.x() + width / 2, center.y() - height / 2);
    }

    public Vector2f getTopRight(Vector2f vector) {
        return vector.set(center.x() + width / 2, center.y() + height / 2);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isOnGround() {
        return center.y() - height / 2 <= 0;
    }

    public boolean isIntersected(Box box) {
        return center.x + width / 2 >= box.center.x - box.width / 2 &&
                center.x - width / 2 <= box.center.x + box.width / 2 &&
                center.y + height / 2 >= box.center.y - box.height / 2 &&
                center.y - height / 2 <= box.center.y + box.height / 2;
    }
}
