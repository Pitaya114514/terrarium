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
    private final ArrayList<Chunk> unloadedChunkHeap = new ArrayList<>();

    private final WorldGenerator generator;
    private final long seed;
    private final Block[] regBlocks;

    public ChunkManager(LocalTerrarium game, WorldGenerator generator, long seed) {
        this.generator = generator;
        this.seed = seed;
        regBlocks = game.getRegisteredBlocks();
    }

    public Block getBlock(int x, int y) {
        int cx = x / 16;
        int cy = y / 16;
        for (Chunk chunk : chunkHeap) {
            Vector2i p = chunk.position;
            if (p.x == cx && p.y == cy) {
                return chunk.blockSquare[Math.abs(x % 16)][Math.abs(y % 16)];
            }
        }
        return null;
    }

    public Chunk load(int x, int y) throws IOException {
        //从内存加载
        for (Chunk chunk : chunkHeap) {
            Vector2i p = chunk.position;
            if (p.x == x && p.y == y) {
                return chunk;
            }
        }
        //从硬盘加载
        IntBuffer[] chunkData = Util.IO.loadChunks();
        if (chunkData != null) {
            for (IntBuffer chunkDatum : chunkData) {
                if (chunkDatum.get() == x && chunkDatum.get() == y) {
                    chunkDatum.position(0);
                    return Util.IO.deserialize(chunkDatum, regBlocks);
                }
            }
        }
        //生成
        Chunk generated = generator.generate(x, y, seed);
        chunkHeap.add(generated);
        return generated;
    }

    public void unload(int x, int y) {
        for (int i = 0; i < chunkHeap.size(); i++) {
            Chunk chunk = chunkHeap.get(i);
            Vector2i p = chunk.position;
            if (p.x == x && p.y == y) {
                unloadedChunkHeap.add(chunk);
                chunkHeap.remove(chunk);
            }
        }
    }

    public void save() throws IOException {
        for (Chunk unloadedChunk : unloadedChunkHeap) {
            Util.IO.saveChunks(Util.IO.serialize(unloadedChunk, regBlocks));
        }
        unloadedChunkHeap.clear();
        unloadedChunkHeap.trimToSize();
    }
}
