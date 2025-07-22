package org.terrarium.core.game.entity;

import org.terrarium.core.game.World;

public abstract class Action {

    public abstract void start(World world);

    public abstract void act(World world);

    public abstract void end(World world);
}
