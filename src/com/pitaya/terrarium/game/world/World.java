package com.pitaya.terrarium.game.world;

import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.barrage.BarrageEntity;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.PlayerEntity;
import com.pitaya.terrarium.game.entity.life.mob.boss.BossEntity;
import com.pitaya.terrarium.game.tool.Counter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.function.Predicate;

public class World implements Runnable {
    public static final Logger LOGGER = LogManager.getLogger(World.class);
    public static final int TICK = 1000 / 60;
    public int gravity;
    public int time;
    public boolean stopFlag = false;
    public final List<Entity> entityList = new ArrayList<>();
    public final List<Vector2f> centerPosList = new ArrayList<>();
    private final List<Entity> syncEntityList = new ArrayList<>();
    public final Chatroom chatroom;
    private Counter tpsCounter;

    private final List<WorldListener> tickEventListeners = new ArrayList<>();
    private final List<WorldListener> disposableTickEventListeners = new ArrayList<>();

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
        addTickEventListener(event -> {
            for (int i = 0; i < entityList.size(); i++) {
                Entity entity = entityList.get(i);
                for (Vector2f centerPos : centerPosList) {
                    if (entity instanceof LivingEntity && centerPos.distance(entity.position) > 3100) {
                        removeEntity(entity);
                    } else if (entity instanceof BarrageEntity && centerPos.distance(entity.position) > 1550) {
                        removeEntity(entity);
                    }
                }
                entity.time++;
                if (entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() <= 0) {
                    if (entity instanceof PlayerEntity) {
                        ((PlayerEntity) entity).setHealth(((PlayerEntity) entity).defaultHealth);
                        entity.position.set(0, 100);
                    } else {
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
//                if (entity.box.damage > 0) {
//                    for (Entity targetEntity : entityList) {
//                        if (entity != targetEntity && targetEntity instanceof LivingEntity) {
//                            boolean xOverlap = (targetEntity.box.getTopRight().x >= entity.box.getBottomLeft().x) && (targetEntity.box.getBottomLeft().x <= entity.box.getTopRight().x);
//                            boolean yOverlap = (targetEntity.box.getTopRight().y >= entity.box.getBottomLeft().y) && (targetEntity.box.getBottomLeft().y <= entity.box.getTopRight().y);
//                            ((LivingEntity) targetEntity).healthManager.isBeingHitting = xOverlap && yOverlap;
//                            if (((LivingEntity) targetEntity).healthManager.isBeingHitting) {
//                                entity.attack((LivingEntity) targetEntity, entity.box.damage);
//                            }
//                        }
//                    }
//                }
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

    private void update() {
        time++;
        entityList.addAll(syncEntityList);
        syncEntityList.clear();
        for (WorldListener listener : tickEventListeners) {
            listener.trigger(new WorldEvent(this));
        }
        disposableTickEventListeners.removeIf(new Predicate<>() {
            @Override
            public boolean test(WorldListener listener) {
                listener.trigger(new WorldEvent(this));
                return true;
            }
        });
    }

    public void addEntity(Entity entity) {
        syncEntityList.add(entity);
        entity.setAlive(true);
        if (entity instanceof BossEntity) {
            chatroom.sendMessage(String.format("%s已苏醒！", entity.name));
        } else if (entity instanceof PlayerEntity) {
            centerPosList.add(entity.position);
        }
    }

    public void removeEntity(Entity entity) {
        entityList.remove(entity);
        entity.setAlive(false);
        if (entity instanceof BossEntity boss && boss.getHealth() <= 0) {
            chatroom.sendMessage(String.format("%s已被打败！", entity.name));
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

    public void addTickEventListener(WorldListener listener) {
        tickEventListeners.add(listener);
    }

    public void addDisposableTickEventListener(WorldListener listener) {
        disposableTickEventListeners.add(listener);
    }
}
