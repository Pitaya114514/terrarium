package com.pitaya.terrarium.client.render;

import com.pitaya.terrarium.Main;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL33;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Hud {
    public static HashSet<HudItem> getDefaultHudItems() {
        HashSet<HudItem> h = new HashSet<>();
        h.add(new HudItem("PlayerHealthBar", true) {
            @Override
            public void render() {
                GL33.glPushMatrix();
                GL33.glColor3f(1.0f, 0.1f, 0.0f);
                GL33.glBegin(GL33.GL_QUADS);
                float health = (float) (5 + Main.getClient().player.entity().getHealth());
                GL33.glVertex2f(5, 5);
                GL33.glVertex2f(5, 20);
                GL33.glVertex2f(health, 20);
                GL33.glVertex2f(health, 5);
                GL33.glEnd();
                GL33.glPopMatrix();
            }
        });
        return h;
    }

    private final HashSet<HudItem> hudItems;
    private boolean isEnable;

    public Hud(HashSet<HudItem> hudItems) {
        this.hudItems = hudItems;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public Set<HudItem> getHudItems() {
        return Collections.unmodifiableSet(hudItems);
    }
}
