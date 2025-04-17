package com.pitaya.terrarium.game.world;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.game.entity.Actionable;
import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.boss.BossEntity;
import com.pitaya.terrarium.game.entity.life.boss.SlimeKingEntity;
import org.joml.Vector2f;

import java.util.HashSet;

public class World implements Runnable {
    public static final int TICK = 1000 / 60;
    public final int gravity;
    public int time;
    public boolean stopFlag = false;
    public HashSet<Entity> entitySet = new HashSet<>();
    public HashSet<Entity> syncEntitySet = new HashSet<>();

    public volatile boolean paused = false;
    public final Object pauseLock = new Object();

    public World(int gravity) {
        this.gravity = gravity;
    }



    @Override
    public void run() {
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
            try {
                Thread.sleep(TICK);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void update() {
        time++;
        entitySet = (HashSet<Entity>) syncEntitySet.clone();
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
        if (time == 100) {
            System.out.println(time);
            SlimeKingEntity entity = new SlimeKingEntity(0, 200);
            entity.setTarget(Main.getClient().player.entity().position);
            addEntity(entity);
        }
    }

    public void addEntity(Entity entity) {
        syncEntitySet.add(entity);
        if (entity instanceof BossEntity) {
            System.out.printf("%s已苏醒！\n", entity.name);
        }
    }

    public void removeEntity(Entity entity) {
        syncEntitySet.remove(entity);
        if (entity instanceof BossEntity) {
            System.out.printf("%s已被打败！\n", entity.name);
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
}
