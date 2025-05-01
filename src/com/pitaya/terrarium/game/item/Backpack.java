package com.pitaya.terrarium.game.item;

public final class Backpack {
    private final int capacity;
    private final Item[] inventory;
    private int index;

    public Backpack(int capacity) {
        this.capacity = capacity;
        inventory = new Item[capacity];
    }

    public void addItem(Item item) {
        if (index >= capacity) {
            return;
        }
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
