package org.terrarium.core.game.world;

public interface WorldGenerator {
    Chunk generate(int x, int y, long seed);
}
