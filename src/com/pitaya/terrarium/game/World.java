package com.pitaya.terrarium.game;

import com.pitaya.terrarium.game.effect.Effect;
import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.barrage.BarrageEntity;
import com.pitaya.terrarium.game.entity.life.HealthManager;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.player.PlayerDifficulty;
import com.pitaya.terrarium.game.entity.life.player.PlayerEntity;
import com.pitaya.terrarium.game.entity.life.mob.boss.BossEntity;
import com.pitaya.terrarium.game.util.Counter;
import com.pitaya.terrarium.game.world.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class World implements Runnable {
    public static final Logger LOGGER = LogManager.getLogger(World.class);
    public static final int TICK = 1000 / 60;
    public final String name;
    public WorldDifficulty difficulty;
    public int gravity;
    public Date date = new Date();
    public boolean stopFlag = false;
    public final List<Entity> entityList = new ArrayList<>();
    public final List<Vector2f> centerPosList = new ArrayList<>();
    public final Chatroom chatroom;
    private Counter tpsCounter;

    private final List<WorldListener> tickEventListeners = new ArrayList<>();
    private final List<WorldListener> disposableTickEventListeners = new ArrayList<>();

    public volatile boolean paused = false;
    public final Object pauseLock = new Object();

    public World(String name, int gravity, WorldDifficulty difficulty) {
        this.difficulty = difficulty == null ? WorldDifficulty.CLASSIC : difficulty;
        this.name = name;
        this.gravity = gravity;
        this.chatroom = new Chatroom();
        chatroom.addListener(event -> {
            List<String> messageList = ((Chatroom) event.getSource()).getMessageList();
            String message = messageList.get(messageList.size() - 1);
            if (message != null) {
                LOGGER.info("{} -> {}", date.toString(), message);
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
                LOGGER.error("e: ", e);
            }
        }
        tpsCounter.cancel();
    }

    private void update() {
        date.addTime();
        for (Entity entity : entityList) {
            for (Vector2f centerPos : centerPosList) {
                if (entity instanceof LivingEntity && centerPos.distance(entity.position) > 3100) {
                    removeEntity(entity);
                } else if (entity instanceof BarrageEntity && centerPos.distance(entity.position) > 1550) {
                    removeEntity(entity);
                }
            }
            if (entity instanceof LivingEntity livingEntity) {
                if (livingEntity.getHealth() <= 0) {
                    if (entity instanceof PlayerEntity player && player.getDifficulty() != PlayerDifficulty.HARDCORE) {
                        if (player.getDifficulty() == PlayerDifficulty.MEDIUMCORE) {
                            player.getBackpack().clear();
                        }
                        player.respawn();
                    } else {
                        removeEntity(entity);
                    }
                }
            }
        }

        for (int i = 0; i < entityList.size(); i++) {
            Entity entity = entityList.get(i);
            entity.time++;
            if (entity instanceof LivingEntity livingEntity) {
                HealthManager hm = livingEntity.healthManager;
                if (hm.invincibilityCD > 0) {
                    hm.invincibilityCD--;
                    hm.isInvincible = true;
                } else {
                    hm.isInvincible = false;
                    if (hm.isWounded) {
                        hm.isWounded = false;
                    }
                }

                for (Effect effect : livingEntity.getEffectSet()) {
                    effect.effect(this);
                }
            }
            if (!entity.moveController.isFloatable()) {
                if (!entity.box.isOnGround()) {
                    entity.moveController.setFloating(true);
                    entity.moveController.addFloatTime();
                    float v = (0.5f * entity.moveController.getGravity() * entity.moveController.getFloatTime()) / 60.0f;
                    float m = entity.getY() - v;
                    if (m - entity.box.getHeight() / 2 < 0) {
                        m -= (m - entity.box.getHeight() / 2);
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
            if (entity instanceof LivingEntity target) {
                for (Entity attacker : entityList) {
                    if (target != attacker && attacker.box.damage > 0 && entity.box.isIntersected(attacker.box)) {
                        attacker.attack(target, attacker.box.damage);
                    }
                }
            }
        }
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
        entityList.add(entity);
        entity.moveController.setGravity(gravity);
        entity.setAlive(true);
        if (entity instanceof BossEntity) {
            chatroom.sendMessage(String.format("%s已苏醒！", entity.name));
        } else if (entity instanceof PlayerEntity) {
            chatroom.sendMessage(String.format("%s已加入。", entity.name));
            centerPosList.add(entity.position);
        }
    }

    public void removeEntity(Entity entity) {
        addDisposableTickEventListener(event -> {
            boolean removed = entityList.remove(entity);
            if (removed) {
                entity.setAlive(false);
                if (entity instanceof BossEntity boss && boss.getHealth() <= 0) {
                    chatroom.sendMessage(String.format("%s已被打败！", entity.name));
                }
            }
        });
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

    @Override
    public String toString() {
        return name;
    }
}
