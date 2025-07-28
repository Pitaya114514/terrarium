package org.terrarium.core.game.entity;

import org.joml.Vector2f;
import org.terrarium.core.game.Attribute;

import java.util.ArrayList;

public final class Entity {
    public static class Factory {
        private final String type;
        private ArrayList<Attribute> attributes;
        private Box box;
        private Action action;
        private double health;
        private double defense;

        public Factory(String type) {
            this.type = type;
        }

        public Factory box(Box box) {
            this.box = box;
            return this;
        }

        public Factory action(Action action) {
            this.action = action;
            return this;
        }

        public Factory health(double health) {
            this.health = health;
            return this;
        }

        public Factory defense(double defense) {
            this.defense = defense;
            return this;
        }

        public Factory attribute(Attribute attribute) {
            if (attributes == null) {
                attributes = new ArrayList<>();
            }
            attributes.add(attribute);
            return this;
        }

        public Entity create() {
            return new Entity(type, box, action, health, defense, attributes);
        }
    }

    public transient final String type;
    public transient final Box box;
    public transient final Action action;
    public transient final LifeManager lifeManager;
    public transient final ArrayList<Attribute> attributes;

    public int floatTime;

    private int id = -1;
    private String name;
    private Vector2f position;

    private Entity(String type, Box box, Action action, double health, double defense, ArrayList<Attribute> otherAttributes) {
        this.type = type;
        this.box = box;
        this.action = action;
        this.lifeManager = health > 0 ? new LifeManager(health, defense) : null;
        this.attributes = otherAttributes;
    }

    public Entity(Entity otherEntity) {
        this.id = otherEntity.id;
        this.type = otherEntity.type;
        this.name = otherEntity.name;
        this.box = otherEntity.box;
        this.action = otherEntity.action;
        this.lifeManager = otherEntity.lifeManager;
        this.floatTime = otherEntity.floatTime;
        if (otherEntity.position != null) {
            this.position = new Vector2f().set(otherEntity.position);
        }
        this.attributes = otherEntity.attributes;
    }

    @Override
    public String toString() {
        return name + "(" + type + ")";
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        if (this.position != null && position == null) {
            return;
        }
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name != null && name == null) {
            return;
        }
        this.name = name;
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
}
