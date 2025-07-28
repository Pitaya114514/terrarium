package org.terrarium.core.game.block;

import org.joml.Vector2i;
import org.terrarium.core.game.Attribute;

import java.util.ArrayList;

public final class Block {
    public static class Factory {
        private final String type;
        private ArrayList<Attribute> attributes;

        public Factory(String type) {
            this.type = type;
        }

        public Factory attribute(Attribute attribute) {
            if (attributes == null) {
                attributes = new ArrayList<>();
            }
            attributes.add(attribute);
            return this;
        }

        public Block create() {
            return new Block(type, attributes);
        }
    }

    public final String type;
    public final ArrayList<Attribute> attributes;

    private int id = -1;
    private Vector2i position;

    private Block(String type, ArrayList<Attribute> otherAttributes) {
        this.type = type;
        this.attributes = otherAttributes;
    }

    public Block(Block otherBlock) {
        this.id = otherBlock.id;
        this.type = otherBlock.type;
        if (otherBlock.position != null) {
            this.position = new Vector2i().set(otherBlock.position);
        }
        this.attributes = otherBlock.attributes;
    }

    public Vector2i getPosition() {
        return position;
    }

    public void setPosition(Vector2i position) {
        if (this.position != null && position == null) {
            return;
        }
        this.position = position;
    }

    public int getId() {
        if (id < 0) throw new IllegalStateException("The block is not initialized");
        return id;
    }

    public void setId(int id) {
        if (this.id < 0 && id >= 0) {
            this.id = id;
        }
    }

    @Override
    public String toString() {
        return type + "(" + position + ")";
    }
}
