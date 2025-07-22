package org.terrarium.core.client;

import org.terrarium.Main;
import org.terrarium.core.game.util.Util;
import org.terrarium.core.game.util.Velocity;
import org.joml.Vector2f;

public final class Camara {
    private float speed = 3.0f;
    public final Vector2f pos = new Vector2f();
    public final float aspectRatio = Float.parseFloat(Main.getClient().configManager.find("client", "game-window-width")) /
            Float.parseFloat(Main.getClient().configManager.find("client", "game-window-height"));
    private boolean isSmoothCamara = Boolean.parseBoolean(Main.getClient().configManager.find("client", "smooth-camara"));
    private int width;
    private int height;
    private final Velocity velocity = new Velocity(speed);
    private final Vector2f returnPos1 = new Vector2f();
    private final Vector2f returnPos2 = new Vector2f();

    public void setHeight(int height) {
        if (height > 0) {
            this.height = height;
            this.width = (int) (aspectRatio * height);
        } else {
            setHeight(Integer.parseInt(Main.getClient().configManager.find("client", "game-window-height")));
        }
    }

    public void setWidth(int width) {
        if (width > 0) {
            this.width = width;
            this.height = (int) (width / aspectRatio);
        } else {
            setWidth(Integer.parseInt(Main.getClient().configManager.find("client", "game-window-width")));
        }
    }

    public Vector2f getRenderPos(Vector2f worldPos) {
        return returnPos1.set(worldPos.x() - (pos.x() - width / 2.0f), (pos.y() + height / 2.0f) - worldPos.y());
    }

    public Vector2f getWorldPos(Vector2f renderPos) {
        return returnPos2.set(renderPos.x() + (pos.x() - width / 2.0f), (pos.y() + height / 2.0f) - renderPos.y());
    }

    public void render(Vector2f pos1, Vector2f pos2, Vector2f pos3, Vector2f pos4, int type, float r, float g, float b) {

    }

    public void setPos(Vector2f pos) {
        if (isSmoothCamara) {
            velocity.speed = 3;
            velocity.radians = Util.Math.getRadians(this.pos, pos);
            double distance = this.pos.distance(pos);
            if (distance < speed) {
                velocity.speed = (float) distance;
            }
            Util.Math.movePos(this.pos, velocity);
        } else {
            this.pos.set(pos);
        }
    }
}
