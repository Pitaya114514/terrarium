package com.pitaya.terrarium.game.entity;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.game.util.PosUtil;
import org.joml.Vector2f;

public class MoveController {
    Vector2f pos;
    private boolean floatable;
    private int floatTime;
    private boolean isFloating;
    private int gravity;


    public MoveController(boolean floatable) {
        this.floatable = floatable;
    }

    public void teleportTo(Vector2f pos) {
        this.pos.set(pos);
    }

    public void teleportTo(float x, float y) {
        pos.set(x, y);
    }

    public void moveTo(Vector2f targetPos, float speed) {
        PosUtil.movePos(pos, PosUtil.getDirection(pos, targetPos), PosUtil.getSlope(pos, targetPos), speed);
    }

    public void moveHorizontallyTo(boolean isRight, float speed) {
        if (isRight) {
            pos.add(speed, 0);
        } else {
            pos.sub(speed, 0);
        }
    }

    public void moveVerticallyTo(boolean isUp, float speed) {
        if (isUp) {
            pos.add(0, speed);
        } else {
            pos.sub(0, speed);
        }
    }

    public void jump(float height) {
        pos.y++;
        floatTime = (int) (0 - (2 * height / Main.getClient().terrarium.getWorldGravity()));
    }

    public boolean isFloatable() {
        return floatable;
    }

    public void addFloatTime() {
        floatTime++;
    }

    public void resetFloatTime() {
        floatTime = 0;
    }

    public int getFloatTime() {
        return floatTime;
    }

    public boolean isFloating() {
        return isFloating;
    }

    public void setFloating(boolean floating) {
        isFloating = floating;
    }

    public void setFloatable(boolean b) {
        floatable = b;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }
}
