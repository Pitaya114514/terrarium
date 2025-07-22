package org.terrarium.core.game.entity.player;

public enum PlayerDifficulty {
    CLASSIC(0), MEDIUMCORE(1), HARDCORE(2);

    public final int code;

    PlayerDifficulty(int code) {
        this.code = code;
    }
}
