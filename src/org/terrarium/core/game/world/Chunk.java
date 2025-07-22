package org.terrarium.core.game.world;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.terrarium.core.game.block.Block;

import java.io.Serializable;
import java.nio.IntBuffer;
import java.util.Arrays;

public class Chunk implements Serializable {
    public record ChunkSource(Vector2i chunkPosition, long seed) {
    }

    public final Vector2i position = new Vector2i();
    public final Block[][] blockSquare = new Block[16][16];
    public final int total;

    public Chunk(Vector2i position, Block[][] blockSquare) {
        this.position.set(position);
        int total = 0;
        for (int i = 0; i < this.blockSquare.length; i++) {
            for (int j = 0; j < this.blockSquare[i].length; j++) {
                Block block = blockSquare[i][j];
                if (block != null) {
                    block.setPosition(new Vector2i(position.x * 16 + i, position.y * 16 + j));
                    this.blockSquare[i][j] = block;
                    total++;
                }
            }
        }
        this.total = total;
    }

    @Override
    public String toString() {
        StringBuilder re = new StringBuilder();
        for (Block[] blocks : blockSquare) {
            re.append(Arrays.toString(blocks));
            re.append("\n");
        }
        return re.toString();
    }
}
