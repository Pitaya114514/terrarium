package org.terrarium.base;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.terrarium.core.game.Attribute;
import org.terrarium.core.game.GameInitializer;
import org.terrarium.core.game.World;
import org.terrarium.core.game.block.Block;
import org.terrarium.core.game.command.Command;
import org.terrarium.core.game.effect.Effect;
import org.terrarium.core.game.entity.Action;
import org.terrarium.core.game.entity.Box;
import org.terrarium.core.game.entity.Entity;
import org.terrarium.core.game.item.Item;
import org.terrarium.core.game.world.Chunk;
import org.terrarium.core.game.world.Structure;
import org.terrarium.core.game.world.WorldGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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
        b[3] = new Block.Factory("tree").attribute(new Attribute("place", 1)).create(); //0-树根 1-树干 2-树梢
        b[4] = new Block.Factory("ice").create();
        b[5] = new Block.Factory("snow").create();
        b[6] = new Block.Factory("sand").create();
        return b;
    }

    @Override
    public Entity[] initEntities() {
        Entity[] e = new Entity[200];
        e[0] = new Entity.Factory("slime").box(new Box(2, 1.4f) {
            @Override
            public void collide(List<Entity> entities) {

            }
        }).action(new Action() {
            @Override
            public void start(World world, Entity entity) {

            }

            @Override
            public void act(World world, Entity entity) {
                for (Entity worldEntity : world.getEntityList()) {
                    if (worldEntity.getId() == 0) {
                        Vector2f ePos = entity.getPosition();
                        Vector2f tPos = worldEntity.getPosition();
                        if (tPos.x > ePos.x) {
                            ePos.add(0.09f, 0);
                        } else if (tPos.x < ePos.x) {
                            ePos.sub(0.09f, 0);
                        }
                    }
                }
            }

            @Override
            public void end(World world, Entity entity) {

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
                    float temperatureCoefficient = x * 16 / 100000f;
                    random.setSeed(seed + x);
                    ArrayList<Structure> structures = new ArrayList<>();
                    Block[][] blockSquare = new Block[16][16];
                    int length = 16;
                    if (y < 1) {
                        for (int i = 0; i < length; i++) {
                            for (int j = 0; j < blockSquare[i].length; j++) {
                                blockSquare[i][j] = new Block(regBlocks[2]);
                            }
                        }
                    } else if (y == 1) {
                        int interval = 0;
                        int lastTop = -1;
                        for (int i = 0; i < length; i++) {
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
                            for (int j = 0; j < length; j++) {
                                if (temperatureCoefficient < 0.6 && temperatureCoefficient > -0.6) {
                                    if (j < top) {
                                        blockSquare[i][j] = new Block(regBlocks[0]);
                                    } else if (j == top) {
                                        blockSquare[i][j] = new Block(regBlocks[1]);
                                        if (random.nextBoolean() && interval >= 3 && j < length - 1) {
                                            Structure tree = new Structure(new Vector2i(x, y), new Vector2i(i, j + 1), 3, 10);
                                            generateTree(tree.elements, random, regBlocks);
                                            structures.add(tree);
                                            interval = 0;
                                        }
                                    }
                                } else if (temperatureCoefficient < -0.6 && temperatureCoefficient > -0.8) {
                                    if (j <= top) blockSquare[i][j] = new Block(regBlocks[5]);
                                } else if (temperatureCoefficient < -0.8) {
                                    if (j <= top) blockSquare[i][j] = new Block(regBlocks[4]);
                                } else if (temperatureCoefficient > 0.6) {
                                    if (j <= top) blockSquare[i][j] = new Block(regBlocks[6]);
                                }

                            }
                            lastTop = top;
                            interval++;
                        }
                    }
                    return new WorldGenerator.Generator(
                            new Chunk(new Vector2i(x, y), blockSquare),
                            structures.toArray(new Structure[0]));
                }
        };
    }

    private void generateTree(Block[][] elements, Random random, Block[] regBlocks) {
        int top = random.nextInt( 3, elements[1].length - 1);
        for (int i = 0; i < top; i++) {
            Block block = new Block(regBlocks[3]);
            block.attributes.get(0).setValue(2);
            elements[1][i] = block;
        }
        Block block = new Block(regBlocks[3]);
        block.attributes.get(0).setValue(2);
        elements[1][top] = block;
    }
}
