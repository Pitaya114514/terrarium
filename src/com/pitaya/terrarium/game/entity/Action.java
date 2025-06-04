package com.pitaya.terrarium.game.entity;

import com.pitaya.terrarium.game.World;
import org.joml.Vector2f;

public abstract class Action {
    protected Entity entity;
    private Vector2f targetPos;
    private Entity targetEntity;

    public Action(Entity entity) {
        this.entity = entity;
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
