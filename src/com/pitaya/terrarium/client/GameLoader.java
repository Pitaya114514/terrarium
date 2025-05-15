package com.pitaya.terrarium.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.entity.life.player.PlayerDifficulty;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.world.WorldLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GameLoader extends WorldLoader {
    private static class PlayerJSON {
        private String name;
        private PlayerDifficulty difficulty;
        private double health;
        private double defense;
        private String[] inventory;
        private int gravity;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public PlayerDifficulty getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(PlayerDifficulty difficulty) {
            this.difficulty = difficulty;
        }

        public double getHealth() {
            return health;
        }

        public void setHealth(double health) {
            this.health = health;
        }

        public double getDefense() {
            return defense;
        }

        public void setDefense(double defense) {
            this.defense = defense;
        }

        public String[] getInventory() {
            return inventory;
        }

        public void setInventory(String[] inventory) {
            this.inventory = inventory;
        }

        public int getGravity() {
            return gravity;
        }

        public void setGravity(int gravity) {
            this.gravity = gravity;
        }
    }
    private final String pathName;

    public GameLoader(String pathName) {
        this.pathName = pathName;
    }

    public String getPathName() {
        return pathName;
    }

    public String exportPlayer(Player player) throws JsonProcessingException {
        PlayerJSON json = new PlayerJSON();
        json.setName(player.entity().name);
        json.setDifficulty(player.entity().getDifficulty());
        json.setHealth(player.entity().getHealth());
        json.setDefense(player.entity().getDefense());
        Item[] inventory = player.entity().getBackpack().getInventory();
        String[] inventoryData = new String[inventory.length];
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                inventoryData[i] = inventory[i].getClass().getName();
            }
        }
        json.setInventory(inventoryData);
        json.setGravity(player.entity().moveController.getGravity());
        return mapper.writeValueAsString(json);
    }


    public void savePlayer(String json) throws IOException {
        PlayerJSON playerJSON = mapper.readValue(json, PlayerJSON.class);
        Files.write(Paths.get( String.format("%s/players/%s.json", pathName, playerJSON.name)), json.getBytes());
    }

    public Player[] scanPlayers() throws IOException {
        File folder = new File(pathName + "/players");
        File[] files = folder.listFiles();
        if (files == null) {
            return null;
        }
        List<Player> playerList = new ArrayList<>();
        List<File> jsonFiles = Arrays.stream(files).filter(file -> file.isFile() && file.getName().endsWith(".json")).toList();
        for (File jsonFile : jsonFiles) {
            playerList.add(importPlayer(jsonFile));
        }
        return playerList.toArray(new Player[0]);
    }

    public WorldData[] scanWorldData() throws IOException {
        File folder = new File(pathName + "/worlds");
        File[] files = folder.listFiles();
        if (files == null) {
            return null;
        }
        List<WorldData> worldDataList = new ArrayList<>();
        List<File> jsonFiles = Arrays.stream(files).filter(file -> file.isFile() && file.getName().endsWith(".json")).toList();
        for (File jsonFile : jsonFiles) {
            WorldData worldData = new WorldData(jsonFile.getName());
            worldData.setData(Files.readString(jsonFile.toPath()));
            worldDataList.add(worldData);
        }
        return worldDataList.toArray(new WorldData[0]);
    }

    public Player importPlayer(File file) throws IOException {
        PlayerJSON playerData = mapper.readValue(file, PlayerJSON.class);
        Player player = new Player(playerData.getName(), playerData.getDifficulty());
        player.entity().setHealth(playerData.getHealth());
        player.entity().setDefense(playerData.getDefense());
        try {
            for (String itemData : playerData.getInventory()) {
                if (itemData != null) {
                    Object item = Class.forName(itemData).getDeclaredConstructor().newInstance();
                    if (item instanceof Item trueItem) {
                        player.entity().getBackpack().addItem(trueItem);
                    }
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException exception) {
            return null;
        }
        player.entity().moveController.setGravity(playerData.getGravity());
        return player;
    }

    public void saveWorld(String json) throws IOException {
        WorldJSON worldJSON = mapper.readValue(json, WorldJSON.class);
        Files.write(Paths.get(String.format("%s/worlds/%s.json", pathName, worldJSON.getName())), json.getBytes());
    }
}
