package com.pitaya.terrarium.game.tool;

import org.joml.Vector2f;

public final class PosTool {
    private PosTool() {
    }

    public static float getSlope(Vector2f yourPos, Vector2f destination) {
        return (destination.y() - yourPos.y()) / (destination.x() - yourPos.x());
    }

    public static boolean getDirection(Vector2f yourPos, Vector2f destination) {
        return destination.x > yourPos.x;
    }

    public static double getDistance(Vector2f pos1, Vector2f pos2) {
        return pos1.distance(pos2);
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
