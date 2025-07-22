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

public class World implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(World.class);
    public static final int TICK = 1000 / 60;

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
        }, 0, 10000);
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
    }

    public void tick() {
        synchronized (chunkTable) {
            for (Map.Entry<Entity, Chunk[]> entry : chunkTable.entrySet()) {
                Entity entity = entry.getKey();
                int cx = (int) entity.getPosition().x / 16;
                int cy = (int) entity.getPosition().y / 16;
                allocateChunk(entry.getValue(), cx, cy, 2, 2);
            }
        }
    }

    private void allocateChunk(Chunk[] chunkArray, int x, int y, int length, int height) {
        int dl = length * 2 + 1;
        int dh = height * 2 + 1;
        if (chunkArray.length < dl * dh) {
            throw new ArrayIndexOutOfBoundsException();
        }
        for (int i = 0; i < dl; i++) {
            for (int j = 0; j < dh; j++) {
                try {
                    chunkArray[i * dl + j] = chunkManager.load(x + j - length, y + i - height);
                } catch (IOException e) {
                    LOGGER.error("Unable to load chunk: ", e);
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
        synchronized (chunkTable) {
            if (entity.attributes.get(0).getValue() instanceof PlayerDifficulty) {
                chunkTable.put(entity, new Chunk[25]);
            }
        }
    }

    public void removeEntity(Entity entity) {
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
}
