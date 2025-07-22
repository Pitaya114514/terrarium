package org.terrarium.core.game;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;

import java.io.Serializable;

public class Attribute implements Serializable {
    private String name;
    private Object value;

    public Attribute(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
