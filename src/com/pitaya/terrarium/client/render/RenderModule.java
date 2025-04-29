package com.pitaya.terrarium.client.render;

import org.joml.Vector2f;

public abstract class RenderModule implements Renderable {
    private String name;
    private boolean isEnable;

    public RenderModule(String name, boolean isEnable) {
        this.name = name;
        this.isEnable = isEnable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    @Override
    public void render(Vector2f windowSize) {
        if (isEnable) {
            renderFunction(windowSize);
        }
    }

    public abstract void renderFunction(Vector2f windowSize);
}
