package com.pitaya.terrarium.game.tool;

import org.joml.Vector2f;

import java.util.concurrent.ThreadLocalRandom;

public final class PosTool {
    private PosTool() {
    }

    public static float getSlope(Vector2f yourPos, Vector2f destination) {
        return (destination.y() - yourPos.y()) / (destination.x() - yourPos.x());
    }

    public static boolean getDirection(Vector2f yourPos, Vector2f destination) {
        return destination.x > yourPos.x;
    }

    public static Vector2f getRandomPos(Vector2f yourPos, float range) {
        float x = ThreadLocalRandom.current().nextFloat() * 2 * range + yourPos.x() - range;
        float y = ThreadLocalRandom.current().nextFloat() * 2 * range + yourPos.y() - range;
        return new Vector2f(x, y);
    }

    public static void movePos(Vector2f yourPos, boolean direction, float slope, float speed) {
        if (Float.isNaN(slope)) {
            return;
        }
        float x, y;
        if (Float.isInfinite(slope)) {
            x = 0;
            y = speed;
            y = slope > 0 ? y : -y;
        } else {
            x = (float) (speed / Math.sqrt(slope * slope + 1));
            x = direction ? x : -x;
            y = slope * x;
        }
        yourPos.x += x;
        yourPos.y += y;
    }
}
