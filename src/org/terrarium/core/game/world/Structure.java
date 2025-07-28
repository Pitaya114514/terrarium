package org.terrarium.core.game.world;

import org.joml.Vector2i;
import org.terrarium.core.game.block.Block;

public class Structure {
    public final Vector2i size;
    public final Block[][] elements;
    private final Vector2i cPosition;
    private final Vector2i bPosition;
    private boolean isGenerated = false;

    public Structure(Vector2i chunkPos, Vector2i blockPos, int width, int height) {
        cPosition = chunkPos;
        bPosition = blockPos;
        this.size = new Vector2i(width, height);
        this.elements = new Block[width][height];
    }

    public Vector2i getChunkPos() {
        return cPosition;
    }

    public Vector2i getChunkBlockPos() {
        return bPosition;
    }

    public Vector2i getBlockPos() {
        return new Vector2i().add(cPosition).mul(16, 16).add(bPosition);
    }

    public void generate() {
        isGenerated = true;
    }

    public boolean isGenerated() {
        return isGenerated;
    }
}
