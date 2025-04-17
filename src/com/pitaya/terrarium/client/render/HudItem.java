package com.pitaya.terrarium.client.render;

import org.joml.Vector2f;

public abstract class HudItem {
    public final String name;
    protected boolean isEnable;

    public HudItem(String name, boolean isEnable) {
        this.name = name;
        this.isEnable = isEnable;
    }

    public abstract void render();

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    @Override
    public String toString() {
        return "HudItem{" + "name='" + name + '\'' + '}';
    }
}
