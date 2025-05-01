package com.pitaya.terrarium.client;

import com.pitaya.terrarium.game.entity.life.PlayerEntity;
import org.joml.Vector2f;

public final class Player {
    private final PlayerEntity entity;
    public final Vector2f cursorPos = new Vector2f();
    public boolean isMovingToLeft;
    public boolean isMovingToRight;
    public int backpackIndex;

    public Player(String name) {
        this.entity = new PlayerEntity(name, 0, 100);
    }

    public PlayerEntity entity() {
        return entity;
    }
}
