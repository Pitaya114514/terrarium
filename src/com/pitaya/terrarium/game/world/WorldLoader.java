package com.pitaya.terrarium.game.world;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.pitaya.terrarium.game.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldLoader {
    public static class WorldJSON {
        private String name;
        private WorldDifficulty difficulty;
        private int gravity;
        private int days;
        private int time;
        private boolean isDaytime;

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
    }

    public final List<World> worldList;
    protected ObjectMapper mapper = new ObjectMapper();

    public WorldLoader() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        worldList = new ArrayList<>();
    }

    public World importWorld(String worldData) throws IOException {
        WorldJSON json = null;
        World world;
        try {
            json = mapper.readValue(worldData, WorldJSON.class);
            world = new World(json.name, json.gravity, json.difficulty);
        } catch (InvalidFormatException e) {
            WorldJSON newJson = mapper.readValue(worldData, WorldJSON.class);
            world = new World(newJson.name, newJson.gravity, WorldDifficulty.CLASSIC);
            json = newJson;
        }
        world.date.setDays(json.getDays());
        world.date.setTime(json.getTime());
        world.date.setDaytime(json.isDaytime());
        return world;
    }

    public String exportWorld(World world) throws JsonProcessingException {
        WorldJSON json = new WorldJSON();
        json.setName(world.name);
        json.setGravity(world.gravity);
        json.setDifficulty(world.difficulty);
        json.setDays(world.date.getDays());
        json.setTime(world.date.getTime());
        json.setDaytime(world.date.isDaytime());
        return mapper.writeValueAsString(json);
    }
}
