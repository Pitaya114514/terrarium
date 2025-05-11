package com.pitaya.terrarium.game.entity.life;

import com.pitaya.terrarium.game.entity.MoveController;

public class PlayerMoveController extends MoveController {
    public boolean isPressingW;
    public boolean isPressingS;

    protected PlayerMoveController(boolean floatable) {
        super(floatable);
    }
}
