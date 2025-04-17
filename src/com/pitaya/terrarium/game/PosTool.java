package com.pitaya.terrarium.game;

import org.joml.Vector2f;

public final class PosTool {
    private PosTool() {
    }

    public static float getSlope(Vector2f yourPos, Vector2f destination) {
        float slope;
        if (destination.x() > yourPos.x() && destination.y() > yourPos.y()) {
            slope = (yourPos.y() - destination.y()) / (yourPos.x() - destination.x());
        } else if (destination.x() < yourPos.x() && destination.y() > yourPos.y()) {
            slope = (yourPos.y() - destination.y()) / (destination.x() - yourPos.x());
        } else if (destination.x() < yourPos.x() && destination.y() < yourPos.y()) {
            slope = (destination.y() - yourPos.y()) / (destination.x() - yourPos.x());
        } else if (destination.x() > yourPos.x() && destination.y() < yourPos.y()) {
            slope = (destination.y() - yourPos.y()) / (yourPos.x() - destination.x());
        } else {
            slope = 0;
        }
        return slope;
    }

    public static int getQuadrant(Vector2f yourPos, Vector2f destination) {
        int quadrant;
        if (destination.x() > yourPos.x() && destination.y() > yourPos.y()) {
            quadrant = 1;
        } else if (destination.x() < yourPos.x() && destination.y() > yourPos.y()) {
            quadrant = 2;
        } else if (destination.x() < yourPos.x() && destination.y() < yourPos.y()) {
            quadrant = 3;
        } else if (destination.x() > yourPos.x() && destination.y() < yourPos.y()) {
            quadrant = 4;
        } else {
            quadrant = 0;
        }
        return quadrant;
    }

    public static double getDistance(Vector2f pos1, Vector2f pos2) {
        return pos1.distance(pos2);
    }

    public static void movePos(Vector2f yourPos, int quadrant, float slope, float speed) {
        float x = (float) (speed / Math.sqrt(slope * slope + 1));
        float y = slope * x;
        switch (quadrant) {
            case 1:
                yourPos.x += x;
                yourPos.y += y;
                break;
            case 2:
                yourPos.x -= x;
                yourPos.y += y;
                break;
            case 3:
                yourPos.x -= x;
                yourPos.y -= y;
                break;
            case 4:
                yourPos.x += x;
                yourPos.y -= y;
                break;
        }
    }
}
