package com.pitaya.terrarium.game.world;

import com.pitaya.terrarium.game.entity.Entity;

import java.util.List;

public class WorldInfo {
    private String name;
    private WorldDifficulty difficulty;
    private int gravity;
    private int days;
    private int time;
    private boolean isDaytime;
    private List<Entity> entityList;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WorldDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(WorldDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Entity> entityList) {
        this.entityList = entityList;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isDaytime() {
        return isDaytime;
    }

    public void setDaytime(boolean daytime) {
        isDaytime = daytime;
    }

    @Override
    public String toString() {
        return name;
    }
}
