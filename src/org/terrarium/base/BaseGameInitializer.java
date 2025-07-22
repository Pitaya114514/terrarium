package org.terrarium.base;

import org.joml.Vector2i;
import org.terrarium.core.game.Attribute;
import org.terrarium.core.game.GameInitializer;
import org.terrarium.core.game.Terrarium;
import org.terrarium.core.game.World;
import org.terrarium.core.game.block.Block;
import org.terrarium.core.game.command.Command;
import org.terrarium.core.game.effect.Effect;
import org.terrarium.core.game.entity.Action;
import org.terrarium.core.game.entity.Box;
import org.terrarium.core.game.entity.Entity;
import org.terrarium.core.game.item.Item;
import org.terrarium.core.game.world.Chunk;
import org.terrarium.core.game.world.WorldGenerator;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BaseGameInitializer implements GameInitializer {
    private final Random random = new Random();

    @Override
    public Command[] initCommands() {
        return new Command[0];
    }

    @Override
    public Block[] initBlocks() {
        Block[] b = new Block[200];
        b[0] = new Block.Factory("dirt").create();
        b[1] = new Block.Factory("grass").create();
        b[2] = new Block.Factory("stone").create();
        return b;
    }

    @Override
    public Entity[] initEntities() {
        Entity[] e = new Entity[200];
        e[0] = new Entity.Factory("slime").box(new Box(30, 25) {
            @Override
            public void collide(List<Entity> entities) {

            }
        }).action(new Action() {
            @Override
            public void start(World world) {

            }

            @Override
            public void act(World world) {

            }

            @Override
            public void end(World world) {

            }
        }).health(50).attribute(new Attribute("color", new Color(21, 13, 250))).create();
        return e;
    }

    @Override
    public Effect[] initEffects() {
        return new Effect[0];
    }

    @Override
    public Item[] initItems() {
        return new Item[0];
    }

    @Override
    public WorldGenerator[] getWorldGenerators(Block[] regBlocks) {
        return new WorldGenerator[]{
                (x, y, seed) -> {
                    random.setSeed(seed + x);
                    Block[][] blockSquare = new Block[16][16];
                    if (y < 1) {
                        for (int i = 0; i < blockSquare.length; i++) {
                            for (int j = 0; j < blockSquare[i].length; j++) {
                                blockSquare[i][j] = new Block(regBlocks[2]);
                            }
                        }
                    } else if (y == 1) {
                        int lastTop = -1;
                        for (int i = 0; i < blockSquare.length; i++) {
                            int top;
                            if (lastTop == -1) {
                                top = random.nextInt(16);
                            } else {
                                top = lastTop;
                            }
                            boolean shouldUp;
                            if (top == 0) {
                                shouldUp = true;
                            } else if (top == 15) {
                                shouldUp = false;
                            } else {
                                shouldUp = random.nextBoolean();
                            }
                            top = shouldUp ? top + 1 : top - 1;
                            for (int j = 0; j < blockSquare.length; j++) {
                                if (j < top) {
                                    blockSquare[i][j] = new Block(regBlocks[0]);
                                } else if (j == top) {
                                    blockSquare[i][j] = new Block(regBlocks[1]);
                                }
                            }
                            lastTop = top;
                        }
                    }
                    return new Chunk(new Vector2i(x, y), blockSquare);
                }
        };
    }
}
