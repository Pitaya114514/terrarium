package org.terrarium.core.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.terrarium.core.game.block.Block;
import org.terrarium.core.game.entity.Entity;
import org.terrarium.core.game.entity.player.PlayerDifficulty;
import org.terrarium.core.game.util.Util;
import org.terrarium.core.game.world.*;
import org.terrarium.core.game.world.Date;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class World implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(World.class);
    public static final int TICK = 1000 / 60;

    private final ExecutorService chunkLoaderPool = Executors.newFixedThreadPool((int) (Runtime.getRuntime().availableProcessors() * 1.5));
    private final Util.Counter tpsCounter = new Util.Counter();
    private final Timer worldTimer = new Timer();
    private final ArrayList<Entity> entityList = new ArrayList<>();
    private final Date date;
    private final Chatroom chatroom;

    private final ChunkManager chunkManager;
    public final HashMap<Entity, Chunk[]> chunkTable = new HashMap<>();

    private String name;
    private WorldDifficulty difficulty;

    public World(String name, WorldDifficulty difficulty, LocalTerrarium terrarium, long seed) {
        this.name = name;
        this.difficulty = difficulty == null ? WorldDifficulty.CLASSIC : difficulty;
        this.date = new Date();
        this.chatroom = new Chatroom();
        chatroom.addListener(event -> {
            List<String> messageList = ((Chatroom) event.getSource()).getMessageList();
            String message = messageList.get(messageList.size() - 1);
            LOGGER.info("{}: {}", date.toString(), message);
        });
        this.chunkManager = new ChunkManager(terrarium, terrarium.getWorldGenerators()[0], seed);
        this.worldTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (chunkManager) {
                    LOGGER.info("Saving the world");
                    try {
                        chunkManager.save();
                    } catch (IOException e) {
                        LOGGER.error("Unable to save world: ", e);
                    }
                    LOGGER.info("Saved the world");
                }

            }
        }, 0, 300000);
        addEntity(new Entity(terrarium.getRegisteredEntities()[1]), new Vector2f(0, 200), "Slime");
    }

    @Override
    public void run() {
        tpsCounter.schedule();
        while (!Thread.currentThread().isInterrupted()) {
            tick();
            tpsCounter.addCount();
            try {
                Thread.sleep(TICK);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        tpsCounter.cancel();
        worldTimer.cancel();
        chunkLoaderPool.shutdown();
    }

    public void tick() {
        synchronized (chunkTable) {
            for (Map.Entry<Entity, Chunk[]> entry : chunkTable.entrySet()) {
                Entity entity = entry.getKey();
                chunkLoaderPool.execute(() -> {
                        try {
                            chunkManager.allocate(
                                    entry.getValue(),
                                    (int) entity.getPosition().x / 16,
                                    (int) entity.getPosition().y / 16,
                                    2, 2);
                        } catch (IOException e) {
                            LOGGER.error("Unable to load chunk: ", e);
                        }
                    }
                );
            }
        }
        synchronized (entityList) {
            for (Entity entity : entityList) {
                if (entity.getId() != 0) {
                    entity.action.act(this, entity);
                    Vector2f position = entity.getPosition();
                    Vector2f size = entity.box.size;
                    boolean shouldFall = true;
                    boolean shouldBreak = false;
                    float fallDistance = (0.5f * 2 * entity.floatTime) / 60.0f;
                    for (int i = 0; i < Math.min(1, fallDistance); i++) {
                        int brx = (int) Math.floor(position.x + size.x / 2);
                        int blx = (int) Math.floor(position.x - size.x / 2);
                        int bby = (int) Math.floor(position.y - size.y / 2);
                        for (int j = 0; j < brx - blx + 1; j++) {
                            if (getBlock(brx - j, bby - i) != null) {
                                shouldFall = false;
                                shouldBreak = true;
                                fallDistance = 0;
                            }
                        }
                        if (shouldBreak) {
                            break;
                        }
                    }
                    if (shouldFall) {
                        entity.floatTime++;
                    } else {
                        entity.floatTime = 0;
                    }
                    if (fallDistance != 0) {
                        position.set(position.x, position.y - fallDistance);
                    }
                }
            }
        }
    }

    public void addEntity(Entity entity, Vector2f pos, String name) {
        entity.setPosition(new Vector2f(pos));
        entity.setName(name);
        synchronized (entityList) {
            entityList.add(entity);
        }
        if (entity.action != null) {
            entity.action.start(this, entity);
        }
        synchronized (chunkTable) {
            if (entity.attributes.get(0).getValue() instanceof PlayerDifficulty) {
                chunkTable.put(entity, new Chunk[25]);
            }
        }
    }

    public void removeEntity(Entity entity) {
        if (entity.action != null) {
            entity.action.end(this, entity);
        }
        synchronized (entityList) {
            entityList.remove(entity);
        }
        synchronized (chunkTable) {
            if (entity.attributes.get(0).getValue() instanceof PlayerDifficulty) {
                chunkTable.remove(entity);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public WorldDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(WorldDifficulty difficulty) {
        if (difficulty != null) {
            this.difficulty = difficulty;
        }
    }

    public Chatroom getChatroom() {
        return chatroom;
    }

    public int getTps() {
        return tpsCounter.getValue();
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public Block getBlock(int x, int y) {
        return chunkManager.getBlock(x, y);
    }

    public void setBlock(int x, int y, Block block) {
        chunkManager.setBlock(x, y, block);
    }


}
