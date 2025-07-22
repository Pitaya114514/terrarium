package org.terrarium.core.client;

import org.joml.Vector2f;
import org.terrarium.core.game.Terrarium;
import org.terrarium.core.game.entity.Box;
import org.terrarium.core.game.entity.Entity;

public final class ActionController {
    private int floatTime;
    public final Vector2f mousePos = new Vector2f();
    public boolean leftKey;
    public boolean leftMouse;
    public boolean rightKey;
    public boolean spaceKey;

    void tick(Player player, Terrarium terrarium) {
        Entity entity = player.getEntity();
        Vector2f position = entity.getPosition();
        Box box = entity.box;
        Vector2f size = entity.box.size;

        int top = 0;
        boolean lb = false;
        boolean rb = false;

        int ltx = (int) (position.x - size.x / 2);
        int lty = (int) (position.y + size.y / 2);
        int rtx = (int) (position.x + size.x / 2);
        int rty = (int) (position.y + size.y / 2);
        int rbx = (int) (position.x + size.x / 2);
        int rby = (int) (position.y - size.y / 2);
        int lbx = (int) (position.x - size.x / 2);
        int lby = (int) (position.y - size.y / 2);
        for (int i = 0; i < rbx - lbx; i++) {
            if (terrarium.getBlock(rbx - i, lby) != null) {
                top = lby;
                break;
            }
        }
        for (int i = 0; i < lty - lby; i++) {
            if (terrarium.getBlock(lbx, lty - i) != null) {
                lb = true;
                break;
            }
        }

        for (int i = 0; i < rty - rby; i++) {
            if (terrarium.getBlock(rbx, rty - i) != null) {
                rb = true;
                break;
            }
        }

        float h = size.y / 2;
        if (position.y - h > top + 1) {
            floatTime++;
            float v = (0.5f * 2 * floatTime) / 60.0f;
            float m = position.y - v;
            position.set(position.x, m);
        } else {
            floatTime = 0;
        }

        if (spaceKey) jump(13);

        if (leftKey && !lb) position.x -= 0.5f;

        if (rightKey && !rb) position.x += 0.5f;

    }

    public void jump(float height) {
        floatTime = (int) (0 - (2 * height / 2));
    }
}
