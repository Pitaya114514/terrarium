package org.terrarium.core.game.world;

public interface WorldGenerator {
    record Generator(Chunk chunk, Structure[] structures) {
    }

    Generator generate(int x, int y, long seed);
}
