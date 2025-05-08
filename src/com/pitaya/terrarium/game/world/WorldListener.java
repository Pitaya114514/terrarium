package com.pitaya.terrarium.game.world;

import java.util.EventListener;

public interface WorldListener extends EventListener {
    void trigger(WorldEvent event);
}
