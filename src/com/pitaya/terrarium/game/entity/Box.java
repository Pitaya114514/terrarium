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
        boolean xOverlap = (box.getTopRight().x >= getBottomLeft().x) && (box.getBottomLeft().x <= getTopRight().x);
        boolean yOverlap = (box.getTopRight().y >= getBottomLeft().y) && (box.getBottomLeft().y <= getTopRight().y);
        return xOverlap && yOverlap;
    }

    public boolean isIntersected(Vector2f point) {
        boolean xOverlap = point.x >= getBottomLeft().x && point.x <= getTopRight().x;
        boolean yOverlap = point.y >= getBottomLeft().x && point.y <= getTopRight().y;
        return xOverlap && yOverlap;
    }
}
