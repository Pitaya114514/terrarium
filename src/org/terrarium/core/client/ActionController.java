package org.terrarium.core.client;

import org.joml.Vector2f;
import org.terrarium.core.game.Terrarium;
import org.terrarium.core.game.entity.Entity;

public final class ActionController {
    private int floatTime;
    public boolean leftKey;
    public boolean rightKey;
    public boolean spaceKey;

    void tick(Player player, Terrarium terrarium) {
        Entity entity = player.getEntity();
        Vector2f position = entity.getPosition();
        Vector2f size = entity.box.size;

        boolean lb = false;
        boolean rb = false;

        float rx = position.x + size.x / 2;
        float lx = position.x - size.x / 2;
        float by = position.y - size.y / 2;
        float ty = position.y + size.y / 2;

        int brx = (int) Math.floor(position.x + size.x / 2);
        int blx = (int) Math.floor(position.x - size.x / 2);
        int bby = (int) Math.floor(position.y - size.y / 2);
        int bty = (int) Math.floor(position.y + size.y / 2);

        boolean shouldFall = true;
        boolean shouldBreak = false;
        float fallDistance = (0.5f * 2 * floatTime) / 60.0f;
        for (int i = 0; i < Math.min(1, fallDistance); i++) {
            for (int j = 0; j < brx - blx + 1; j++) {
                if (terrarium.getBlock(brx - j, bby - i) != null) {
                    shouldFall = false;
                    shouldBreak = true;
                    fallDistance = 0;
                }
            }
            if (shouldBreak) {
                break;
            }
        }
        if (shouldFall) {
            floatTime++;
        } else {
            floatTime = 0;
        }

        if (fallDistance != 0) {
            position.set(position.x, position.y - fallDistance);
        }

        float moveSpeed = 100f;
        if (leftKey) {
            boolean shouldMove = true;
            for (int i = 0; i < bty - bby; i++) {
                if (terrarium.getBlock((int) Math.floor(lx - moveSpeed), bty - i) != null) {
                    shouldMove = false;
                    break;
                }
            }
            if (shouldMove) {
                position.x -= moveSpeed;
            } else {
                position.x = (float) Math.floor(position.x) + moveSpeed / 10;
            }
        }

        if (rightKey) {
            boolean shouldMove = true;
            for (int i = 0; i < bty - bby; i++) {
                if (terrarium.getBlock((int) Math.floor(rx + moveSpeed), bty - i) != null) {
                    shouldMove = false;
                    break;
                }
            }
            if (shouldMove) {
                position.x += moveSpeed;
            } else {
                position.x = (float) Math.ceil(position.x) - moveSpeed / 10;
            }
        }

        if (spaceKey) jump(13);

    }

    public void jump(float height) {
        floatTime = (int) (0 - (2 * height / 2));
    }
}
