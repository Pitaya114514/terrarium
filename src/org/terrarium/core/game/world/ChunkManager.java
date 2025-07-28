package org.terrarium.core.game.world;

import org.joml.Vector2i;
import org.terrarium.core.game.LocalTerrarium;
import org.terrarium.core.game.block.Block;
import org.terrarium.core.game.util.Util;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class ChunkManager {
    private final ArrayList<Chunk> chunkHeap = new ArrayList<>();
    private final WorldGenerator generator;
    private final long seed;
    private final Block[] regBlocks;

    public ChunkManager(LocalTerrarium game, WorldGenerator generator, long seed) {
        this.generator = generator;
        this.seed = seed;
        regBlocks = game.getRegisteredBlocks();
    }

    public Block getBlock(int x, int y) {
        int cx = (int) Math.floor(x / 16f);
        int cy = (int) Math.floor(y / 16f);
        for (Chunk chunk : chunkHeap) {
            if (chunk.position.equals(cx, cy)) {
                return chunk.blockSquare[x - cx * 16][y - cy * 16];
            }
        }
        return null;
    }

    public void setBlock(int x, int y, Block block) {
        int cx = (int) Math.floor(x / 16f);
        int cy = (int) Math.floor(y / 16f);
        for (Chunk chunk : chunkHeap) {
            if (chunk.position.equals(cx, cy)) {
                if (block != null) {
                    block.setPosition(new Vector2i(x, y));
                }
                chunk.blockSquare[x - cx * 16][y - cy * 16] = block;
            }
        }
    }

    public synchronized void save() throws IOException {
        for (Chunk chunk : chunkHeap) {
            Util.IO.saveChunks(Util.IO.serialize(chunk));
        }
    }

    public void allocate(Chunk[] chunkArray, int x, int y, int length, int height) throws IOException {
        int dl = length * 2 + 1;
        int dh = height * 2 + 1;
        if (chunkArray.length < dl * dh) {
            throw new ArrayIndexOutOfBoundsException();
        }
        for (int i = 0; i < dl; i++) {
            for (int j = 0; j < dh; j++) {
                int chunkX = x + j - length;
                int chunkY = y + i - height;
                chunkArray[i * dl + j] = load(chunkX, chunkY);
            }
        }
    }

    public synchronized Chunk load(int x, int y) throws IOException {
        for (Chunk chunk : chunkHeap) {
            if (chunk.position.equals(x, y)) {
                return chunk;
            }
        }

        IntBuffer[] chunkData = Util.IO.loadChunks();
        if (chunkData != null) {
            for (IntBuffer chunkDatum : chunkData) {
                if (chunkDatum.get() == x && chunkDatum.get() == y) {
                    chunkDatum.position(0);
                    Chunk deserialized = Util.IO.deserialize(chunkDatum, regBlocks);
                    chunkHeap.add(deserialized);
                    return deserialized;
                }
            }
        }

        WorldGenerator.Generator generated = generator.generate(x, y, seed);
        Chunk chunk = generated.chunk();
        Structure[] structures = generated.structures();
        for (Structure structure : structures) {
            if (structure.isGenerated()) continue;
            Vector2i blockPos = structure.getChunkBlockPos();
            Block[][] elements = structure.elements;
            final int chunkSize = 16;
            for (int i = 0; i < elements.length; i++) {
                int bx = blockPos.x + i;
                int cx = x + (bx / chunkSize);
                bx %= chunkSize;

                for (int j = 0; j < elements[i].length; j++) {
                    int by = blockPos.y + j;
                    int cy = y + (by / chunkSize);
                    by %= chunkSize;

                    Chunk targetChunk = (cx == x && cy == y) ? chunk : load(cx, cy);
                    Block block = elements[i][j];

                    if (block != null) {
                        targetChunk.blockSquare[bx][by] = block;
                        block.setPosition(new Vector2i(cx * chunkSize + bx, cy * chunkSize + by));
                    } else {
                        targetChunk.blockSquare[bx][by] = null;
                    }
                }
            }
            structure.generate();
        }
        chunkHeap.add(chunk);
        return chunk;
    }
}
