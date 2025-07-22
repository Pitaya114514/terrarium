package org.terrarium.core.client;

import org.joml.Vector2f;
import org.terrarium.core.game.Terrarium;
import org.terrarium.core.game.entity.player.PlayerDifficulty;
import org.terrarium.core.game.entity.Entity;

public final class Player {
    public final Vector2f cursorPos = new Vector2f();
    public final String name;
    private final PlayerDifficulty difficulty;
    private Entity entity;
    public int backpackIndex;

    public Player(String name, PlayerDifficulty difficulty) {
        this.name = name;
        this.difficulty = difficulty;
    }

    public void createEntity(Terrarium terrarium) {
        Entity player = new Entity(terrarium.getRegisteredEntities()[0]);
        player.setName(name);
        player.attributes.get(0).setValue(difficulty);
        this.entity = player;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public String toString() {
        return name;
    }
}
