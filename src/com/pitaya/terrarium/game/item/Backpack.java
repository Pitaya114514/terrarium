package com.pitaya.terrarium.game.item;

import com.pitaya.terrarium.game.entity.life.PlayerEntity;

public final class Backpack {
    private PlayerEntity owner;
    private final int capacity;
    private final Item[] inventory;
    private int index;

    public Backpack(PlayerEntity owner, int capacity) {
        this.owner = owner;
        this.capacity = capacity;
        inventory = new Item[capacity];
    }

    public void addItem(Item item) {
        if (index >= capacity) {
            return;
        }
        item.setOwner(owner);
        inventory[index] = item;
        index++;
    }

    public void addItem(Item item, int index) {
        if (index >= capacity) {
            return;
        }
        inventory[index] = item;
    }

    public Item getItem(int index) {
        if (index >= capacity) {
            return null;
        }
        return inventory[index];
    }
}
