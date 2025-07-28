package org.terrarium.core.game.entity;

import org.terrarium.core.game.World;

public interface Action {

    void start(World world, Entity entity);

    void act(World world, Entity entity);

    void end(World world, Entity entity);
}
