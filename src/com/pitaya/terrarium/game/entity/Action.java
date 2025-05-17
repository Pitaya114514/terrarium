package com.pitaya.terrarium.game.entity;

import com.pitaya.terrarium.game.World;
import org.joml.Vector2f;

public abstract class Action {
    protected Vector2f position;
    private Vector2f targetPos;
    private Entity targetEntity;

    public Action(Vector2f position) {
        this.position = position;
    }

    public abstract void start(World world);

    public abstract void act(World world);

    public abstract void end(World world);

    public Vector2f getTargetPos() {
        return targetPos;
    }

    public void setTargetPos(Vector2f targetPos) {
        this.targetPos = targetPos;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(Entity targetEntity) {
        this.targetEntity = targetEntity;
        setTargetPos(targetEntity.position);
    }
}
