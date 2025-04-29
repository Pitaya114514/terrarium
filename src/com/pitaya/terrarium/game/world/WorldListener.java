package com.pitaya.terrarium.game.world;

import com.pitaya.terrarium.game.entity.Entity;

import java.util.EventListener;
import java.util.Set;

public interface WorldListener extends EventListener {
    void trigger(WorldEvent event);

}
