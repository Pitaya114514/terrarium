package com.pitaya.terrarium.client.render.hud;

import com.pitaya.terrarium.client.render.RenderModule;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL33;

public class CharBar extends RenderModule {
    private StringBuilder text = new StringBuilder();

    public CharBar(boolean isEnable) {
        super("CharBar", isEnable);
    }

    @Override
    public void renderFunction(Vector2f windowSize) {
        GL33.glPushMatrix();
        GL33.glColor3f(0.0f, 0.3f, 0.0f);
        GL33.glBegin(GL33.GL_QUADS);
        GL33.glVertex2f(2, 400);
        GL33.glVertex2f(2, 450);
        GL33.glVertex2f(798, 450);
        GL33.glVertex2f(798, 400);
        GL33.glEnd();
        GL33.glPopMatrix();
    }

    public void input(String text) {
        this.text.append(text);
    }

    public void input(char character) {
        this.text.append(character);
    }

    public void clear() {
        text.setLength(0);
    }

    @Override
    public String toString() {
        return text.toString();
    }
}
