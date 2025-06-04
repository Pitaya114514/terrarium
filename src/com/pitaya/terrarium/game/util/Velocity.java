package com.pitaya.terrarium.game.util;

public class Velocity {
    public float speed;
    public double radians;

    public Velocity(float speed, double radians) {
        this.speed = speed;
        this.radians = radians;
    }

    public Velocity(float speed) {
        this.speed = speed;
    }

    public Velocity(double radians) {
        this.radians = radians;
    }

    public Velocity() {
    }

    public double degrees() {
        return Math.toDegrees(radians);
    }

    public float speed() {
        return speed;
    }

    public double radians() {
        return radians;
    }
}
