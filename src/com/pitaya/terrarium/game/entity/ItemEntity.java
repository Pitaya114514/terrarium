package com.pitaya.terrarium.game.entity;

import com.pitaya.terrarium.game.item.Item;
import org.joml.Vector2f;

public class ItemEntity extends Entity {
    public ItemEntity(Item item, Vector2f position) {
        super(item.name + "Entity", new Box(20, 20, 0), new MoveController(false), position, EntityGroups.DEFAULT);
    }
}
