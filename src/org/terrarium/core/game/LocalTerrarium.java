package org.terrarium.core.game;

import com.google.common.collect.ObjectArrays;
import org.joml.Vector2f;
import org.terrarium.core.game.entity.player.PlayerDifficulty;
import org.terrarium.core.game.block.Block;
import org.terrarium.core.game.command.Command;
import org.terrarium.core.game.effect.Effect;
import org.terrarium.core.game.entity.Box;
import org.terrarium.core.game.entity.Entity;
import org.terrarium.core.game.item.Item;
import org.terrarium.core.game.world.Chunk;
import org.terrarium.core.game.world.WorldDifficulty;
import org.terrarium.core.game.world.WorldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

public class LocalTerrarium implements Terrarium {
    public static final Logger LOGGER = LogManager.getLogger(LocalTerrarium.class);

    public static Entity.Factory getPlayerEntityFactory() {
        return new Entity.Factory("Player")
                .box(new Box(2, 3) {

                    @Override
                    public void collide(List<Entity> entities) {

                    }
                })
                .action(null)
                .health(100)
                .attribute(new Attribute("difficulty", PlayerDifficulty.CLASSIC))
                .attribute(new Attribute("backpack", new Item[30]));
    }

    private final Command[] registeredCommands;
    private final Block[] registeredBlocks;
    private final Entity[] registeredEntities;
    private final Effect[] registeredEffects;
    private final Item[] registeredItems;
    private final WorldGenerator[] worldGenerators;
    private Thread worldThread;
    private World mainWorld;

    public LocalTerrarium(GameInitializer initializer) {
        this.registeredCommands = initializer.initCommands();
        this.registeredBlocks = initializer.initBlocks();
        this.registeredEntities = ObjectArrays.concat(getPlayerEntityFactory().create(), initializer.initEntities());
        this.registeredEffects = initializer.initEffects();
        this.registeredItems = initializer.initItems();
        this.worldGenerators = initializer.getWorldGenerators(registeredBlocks);
    }

    public void startWorld(long seed) {
        mainWorld = new World("MainWorld", WorldDifficulty.MASTER, this, seed);
        worldThread = new Thread(mainWorld);
        worldThread.setName(String.format("World-%s", mainWorld.hashCode()));
        worldThread.start();
    }

    public void stopWorld() {
        worldThread.interrupt();
    }

    @Override
    public int executeCommand(String context) {
        return 0;
    }

    @Override
    public void addEntity(Entity entity, Vector2f pos, String name) {
        mainWorld.addEntity(entity, pos, name);
    }

    @Override
    public void useItem(Item item) {

    }

    @Override
    public void sendMassage(Object sender, String message) {
        mainWorld.getChatroom().sendMessage(sender, message);
    }

    @Override
    public int getTps() {
        return mainWorld.getTps();
    }

    @Override
    public String getWorldName() {
        return mainWorld.getName();
    }

    @Override
    public Command[] getRegisteredCommands() {
        return registeredCommands.clone();
    }

    @Override
    public Block[] getRegisteredBlocks() {
        return registeredBlocks.clone();
    }

    @Override
    public Entity[] getRegisteredEntities() {
        return registeredEntities.clone();
    }

    @Override
    public Effect[] getRegisteredEffects() {
        return registeredEffects.clone();
    }

    @Override
    public Item[] getRegisteredItems() {
        return registeredItems.clone();
    }

    public WorldGenerator[] getWorldGenerators() {
        return worldGenerators.clone();
    }

    @Override
    public List<Entity> getEntityList() {
        return mainWorld.getEntityList();
    }

    @Override
    public Map<Entity, Chunk[]> getChunkMap() {
        return mainWorld.chunkTable;
    }

    @Override
    public Block getBlock(int x, int y) {
        return mainWorld.getBlock(x, y);
    }

    public void save() {
        LOGGER.info("Saving world: {}", mainWorld);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(mainWorld.getName() + ".wld"))) {
            for (Map.Entry<Entity, Chunk[]> entry : mainWorld.chunkTable.entrySet()) {
                for (Chunk chunk : entry.getValue()) {
                    oos.writeObject(chunk);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Unable to save world: ", e);
        }
        LOGGER.info("{} is saved", mainWorld);
    }

    public World load() {
        return null;
    }

    //    final List<Command> getCommandList() {
//        return Set.of(new Command("help") {
//            @Override
//            public int execute(String[] args) {
//                LOGGER.info(registeredCommands);
//                return 0;
//            }
//        }, new Command("say") {
//            @Override
//            public int execute(String[] args) {
//                if (args.length >= 1) {
//                    mainWorld.chatroom.sendMessage("[Local]", String.join(" ", args));
//                    return 0;
//                } else {
//                    return -1;
//                }
//            }
//        }, new Command("stop") {
//            @Override
//            public int execute(String[] args) {
//                terminateWorld();
//                return 0;
//            }
//        }, new Command("give") {
//            @Override
//            public int execute(String[] args) {
//                if (args.length >= 2) {
//                    return 0;
//                } else {
//                    return -1;
//                }
//            }
//        }, new Command("print", new Command("entities") {
//            @Override
//            public int execute(String[] args) {
//                LOGGER.info(Util.IO.format(mainWorld.entityList));
//                return 0;
//            }
//        }, new Command("difficulty") {
//            @Override
//            public int execute(String[] args) {
//                LOGGER.info(mainWorld.difficulty);
//                return 0;
//            }
//        }) {
//            @Override
//            public int execute(String[] args) {
//                return Command.execute(String.join(" ", args), this.getCommandSet());
//            }
//        });
//    }
}
