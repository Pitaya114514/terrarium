package org.terrarium.core.game.block;

import org.joml.Vector2i;
import org.terrarium.core.game.Attribute;
import org.terrarium.core.game.entity.Box;
import org.terrarium.core.game.entity.Entity;

import java.util.ArrayList;
import java.util.List;

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
    public final Box box = new Box(1, 1) {
        @Override
        public void collide(List<Entity> entities) {

        }
    };
    public final ArrayList<Attribute> attributes;

    private Vector2i position;

    private Block(String type, ArrayList<Attribute> otherAttributes) {
        this.type = type;

        this.attributes = otherAttributes;
    }

    public Block(Block otherBlock) {
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

    @Override
    public String toString() {
        return type + "(" + position + ")";
    }
}
