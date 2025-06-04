package com.pitaya.terrarium.game.entity.life.player;

public enum PlayerDifficulty {
    CLASSIC(0), MEDIUMCORE(1), HARDCORE(2);

    public final int code;

    PlayerDifficulty(int code) {
        this.code = code;
    }
}
