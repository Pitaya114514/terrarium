package com.pitaya.terrarium.client;

import com.pitaya.terrarium.game.Terrarium;
import com.pitaya.terrarium.game.entity.life.PlayerEntity;
import org.joml.Vector2f;

public final class Player {
    private final Terrarium game;
    private final PlayerEntity entity;
    public boolean isMovingToLeft;
    public boolean isMovingToRight;

    public Player(Terrarium game, String name) {
        this.game = game;
        this.entity = new PlayerEntity(name, 0, 100);
    }

    public PlayerEntity entity() {
        return entity;
    }

    public void setPos(Vector2f pos) {
        entity.moveController.teleportTo(pos);
    }

    public void setPos(float x, float y) {
        entity.moveController.teleportTo(x, y);
    }
}
