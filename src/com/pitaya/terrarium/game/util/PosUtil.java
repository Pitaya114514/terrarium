package com.pitaya.terrarium.game.util;

import org.joml.Vector2f;

import java.util.concurrent.ThreadLocalRandom;

public final class PosUtil {
    public static double HALF_OF_PI = Math.PI / 2;

    private PosUtil() {

    }

    public static Vector2f getRandomPos(Vector2f yourPos, float range) {
        float x = ThreadLocalRandom.current().nextFloat() * 2 * range + yourPos.x() - range;
        float y = ThreadLocalRandom.current().nextFloat() * 2 * range + yourPos.y() - range;
        return new Vector2f(x, y);
    }

    public static void movePos(Vector2f yourPos, Velocity velocity) {
        float slope = (float) Math.tan(velocity.radians);
        float x = (float) (velocity.speed / Math.sqrt(slope * slope + 1));
        float y = slope * x;
        if (velocity.radians >= -HALF_OF_PI && velocity.radians <= HALF_OF_PI) {
            yourPos.x += x;
            yourPos.y += y;
        } else {
            yourPos.x -= x;
            yourPos.y -= y;
        }

    }

    public static double getRadians(Vector2f yourPos, Vector2f targetPos) {
        return Math.atan2(targetPos.y - yourPos.y, targetPos.x - yourPos.x);
    }
}
