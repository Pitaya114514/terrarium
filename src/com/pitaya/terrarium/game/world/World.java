package com.pitaya.terrarium.game.world;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.boss.BossEntity;
import com.pitaya.terrarium.game.entity.life.boss.SlimeKingEntity;
import com.pitaya.terrarium.game.tool.Counter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class World implements Runnable {
    public static final Logger LOGGER = LogManager.getLogger(World.class);
    public static final int TICK = 1000 / 60;
    public int gravity;
    public int time;
    public boolean stopFlag = false;
    public HashSet<Entity> entitySet = new HashSet<>();
    public HashSet<Entity> syncEntitySet = new HashSet<>();
    public final Chatroom chatroom;
    private Counter tpsCounter;

    private final List<WorldListener> tickEventListeners = new ArrayList<>();

    public volatile boolean paused = false;
    public final Object pauseLock = new Object();

    public World(int gravity) {
        this.gravity = gravity;
        this.chatroom = new Chatroom();
        chatroom.addListener(event -> {
            String message = ((Chatroom) event.getSource()).getMessageList().getLast();
            if (message != null) {
                LOGGER.info("{} -> {}", this, message);
            }
        });
        addTickEventListeners(event -> {
            for (Entity entity : entitySet) {
                entity.time++;
                if (entity instanceof LivingEntity) {
                    if (((LivingEntity) entity).getHealth() <= 0) {
                        removeEntity(entity);
                    }
                }
                if (!entity.moveController.isFloatable()) {
                    if (!entity.box.isOnGround()) {
                        entity.moveController.setFloating(true);
                        entity.moveController.addFloatTime();
                        float v = (0.5f * gravity * entity.moveController.getFloatTime()) / 60.0f;
                        float m = entity.getY() - v;
                        if (m - entity.box.height / 2 < 0) {
                            m -= (m - entity.box.height / 2);
                        }
                        entity.moveController.teleportTo(entity.getX(), m);
                    } else {
                        entity.moveController.setFloating(false);
                        entity.moveController.resetFloatTime();
                    }
                }
                if (entity instanceof Actionable) {
                    ((Actionable) entity).action(this);
                }
                if (entity.box.damage > 0) {
                    for (Entity targetEntity : entitySet) {
                        if (entity != targetEntity && targetEntity instanceof LivingEntity) {
                            boolean xOverlap = (targetEntity.box.getTopRight().x >= entity.box.getBottomLeft().x) && (targetEntity.box.getBottomLeft().x <= entity.box.getTopRight().x);
                            boolean yOverlap = (targetEntity.box.getTopRight().y >= entity.box.getBottomLeft().y) && (targetEntity.box.getBottomLeft().y <= entity.box.getTopRight().y);
                            ((LivingEntity) targetEntity).healthManager.isBeingHitting = xOverlap && yOverlap;
                            if (((LivingEntity) targetEntity).healthManager.isBeingHitting) {
                                entity.attack((LivingEntity) targetEntity, entity.box.damage);
                            }
                        }
                    }
                }
            }
        });
        addTickEventListeners(event -> {
            if (time == 100) {
                SlimeKingEntity entity = new SlimeKingEntity(0, 200);
                addEntity(entity);
            }
        });
    }

    @Override
    public void run() {
        tpsCounter = new Counter();
        tpsCounter.schedule();
        while (!stopFlag && !Thread.currentThread().isInterrupted()) {
            synchronized (pauseLock) {
                while (paused) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            update();
            tpsCounter.addCount();
            try {
                Thread.sleep(TICK);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        tpsCounter.cancel();
    }

    @SuppressWarnings("unchecked")
    private void update() {
        time++;
        entitySet = (HashSet<Entity>) syncEntitySet.clone();
        for (WorldListener listener : tickEventListeners) {
            listener.trigger(new WorldEvent(this));
        }
    }

    public void addEntity(Entity entity) {
        syncEntitySet.add(entity);
        if (entity instanceof BossEntity) {
            chatroom.sendMessage(String.format("%s已苏醒！", entity.name));
        }
    }

    public void removeEntity(Entity entity) {
        syncEntitySet.remove(entity);
        if (entity instanceof BossEntity) {
            chatroom.sendMessage(String.format("%s已被打败！\n", entity.name));
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    public void stop() {
        stopFlag = true;
    }

    public int getTps() {
        if (tpsCounter == null) {
            return 0;
        }
        return tpsCounter.getValue();
    }

    public void addTickEventListeners(WorldListener listener) {
        tickEventListeners.add(listener);
    }
}
