package org.terrarium.core.game.world;

public enum WorldDifficulty {
    CLASSIC(0), EXPERT(1), MASTER(2);

    public final int code;

    WorldDifficulty(int code) {
        this.code = code;
    }
}
