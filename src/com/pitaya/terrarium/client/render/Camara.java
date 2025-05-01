package com.pitaya.terrarium.client.render;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.game.tool.PosTool;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL33;

public final class Camara {
    private Vector2f pos = new Vector2f();
    public final float aspectRatio = Float.parseFloat(Main.getClient().properties.getProperty("game-width")) /
            Float.parseFloat(Main.getClient().properties.getProperty("game-height"));
    private boolean isSmoothCamara = Boolean.parseBoolean(Main.getClient().properties.getProperty("smooth-camara"));
    private int width;
    private int height;
    private final Vector2f returnPos1 = new Vector2f();
    private final Vector2f returnPos2 = new Vector2f();

    public void setHeight(int height) {
        if (height > 0) {
            this.height = height;
            this.width = (int) (aspectRatio * height);
        } else {
            setHeight(Integer.parseInt(Main.getClient().properties.getProperty("game-height")));
        }
    }

    public void setWidth(int width) {
        if (width > 0) {
            this.width = width;
            this.height = (int) (width / aspectRatio);
        } else {
            setWidth(Integer.parseInt(Main.getClient().properties.getProperty("game-width")));
        }
    }

    public Vector2f getRenderPos(Vector2f worldPos) {
        return returnPos1.set(worldPos.x() - (pos.x() - width / 2.0f), (pos.y() + height / 2.0f) - worldPos.y());
    }

    public Vector2f getWorldPos(Vector2f renderPos) {
        return returnPos2.set(renderPos.x() + (pos.x() - width / 2.0f), (pos.y() + height / 2.0f) - renderPos.y());
    }

    public void render(Vector2f pos1, Vector2f pos2, Vector2f pos3, Vector2f pos4, int type, float r, float g, float b) {
        GL33.glPushMatrix();
        GL33.glColor3f(r,g,b);
        GL33.glBegin(type);
        GL33.glVertex2f(getRenderPos(pos1).x(), getRenderPos(pos1).y());
        GL33.glVertex2f(getRenderPos(pos2).x(), getRenderPos(pos2).y());
        if (type == GL33.GL_TRIANGLES) {
            GL33.glVertex2f(getRenderPos(pos3).x(), getRenderPos(pos3).y());
        }
        if (type == GL33.GL_QUADS) {
            GL33.glVertex2f(getRenderPos(pos3).x(), getRenderPos(pos3).y());
            GL33.glVertex2f(getRenderPos(pos4).x(), getRenderPos(pos4).y());
        }
        GL33.glEnd();
        GL33.glPopMatrix();
    }

    public void render(Renderable renderable, Vector2f windowSize) {
        renderable.render(windowSize);
    }

    public void setPos(Vector2f pos) {
        if (isSmoothCamara) {
            boolean direction = PosTool.getDirection(this.pos, pos);
            float slope = PosTool.getSlope(this.pos, pos);
            double distance = this.pos.distance(pos);
            if (distance < 3.0f) {
                PosTool.movePos(this.pos, direction, slope, (float) distance);
            } else {
                PosTool.movePos(this.pos, direction, slope, 3.0f);
            }
        } else {
            this.pos.set(pos);
        }
    }
}
