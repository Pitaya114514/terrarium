package com.pitaya.terrarium.client;

import com.pitaya.terrarium.game.entity.life.player.PlayerDifficulty;
import com.pitaya.terrarium.game.entity.life.player.PlayerEntity;
import org.joml.Vector2f;

public final class Player {
    private final PlayerEntity entity;
    public final Vector2f cursorPos = new Vector2f();
    public boolean isMovingToLeft;
    public boolean isMovingToRight;
    public int backpackIndex;

    public Player(String name, PlayerDifficulty difficulty) {
        this.entity = new PlayerEntity(name, new Vector2f(0, 100), difficulty);
    }

    public PlayerEntity entity() {
        return entity;
    }

    @Override
    public String toString() {
        return entity.name;
    }
}
