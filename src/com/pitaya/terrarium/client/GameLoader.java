package com.pitaya.terrarium.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pitaya.terrarium.client.network.Server;
import com.pitaya.terrarium.game.entity.life.player.PlayerDifficulty;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.world.WorldDifficulty;
import com.pitaya.terrarium.game.world.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GameLoader {
    private static final Logger LOGGER = LogManager.getLogger(GameLoader.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

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
    private static class ServerJSON {
        private String name;
        private String address;
        private int port;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    private static String pathName;

    public static String getPath() {
        return pathName;
    }

    public static void setPath(String pathName) {
        GameLoader.pathName = pathName;
    }

    public static Player importPlayer(File file) throws Exception {
        PlayerJSON playerData = mapper.readValue(file, PlayerJSON.class);
        Player player = new Player(playerData.getName(), playerData.getDifficulty());
        player.entity().setHealth(playerData.getHealth());
        player.entity().setDefense(playerData.getDefense());
        for (String itemData : playerData.getInventory()) {
            if (itemData != null) {
                Object item = Class.forName(itemData).getDeclaredConstructor().newInstance();
                if (item instanceof Item trueItem) {
                    player.entity().getBackpack().addItem(trueItem);
                }
            }
        }
        player.entity().moveController.setGravity(playerData.getGravity());
        return player;
    }

    public static String exportPlayer(Player player) throws JsonProcessingException {
        PlayerJSON json = new PlayerJSON();
        json.setName(player.entity().getName());
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


    public static void savePlayer(String json) throws IOException {
        PlayerJSON playerJSON = mapper.readValue(json, PlayerJSON.class);
        Files.write(Paths.get( String.format("%s/players/%s.json", pathName, playerJSON.getName())), json.getBytes());
    }

    public static Player[] scanPlayers() throws Exception {
        File folder = new File(pathName + "/players");
        File[] files = folder.listFiles();
        if (files == null) {
            return new Player[0];
        }
        List<Player> playerList = new ArrayList<>();
        List<File> jsonFiles = Arrays.stream(files).filter(file -> file.isFile() && file.getName().endsWith(".json")).toList();
        for (File jsonFile : jsonFiles) {
            try {
                Player e = importPlayer(jsonFile);
                playerList.add(e);
            } catch (Exception e) {
                if (e instanceof JsonProcessingException) {
                    LOGGER.error("Invalid or corrupted player data: {}", jsonFile.getName(), e);
                } else {
                    throw e;
                }
            }
        }
        return playerList.toArray(new Player[0]);
    }

    public static WorldInfo importWorld(File file) throws IOException {
        GameLoader.WorldJSON worldData = mapper.readValue(file, GameLoader.WorldJSON.class);
        WorldInfo worldInfo = new WorldInfo();
        worldInfo.setName(worldData.getName());
        worldInfo.setDifficulty(worldData.getDifficulty());
        worldInfo.setEntityList(new ArrayList<>());
        worldInfo.setGravity(worldData.getGravity());
        worldInfo.setDays(worldData.getDays());
        worldInfo.setTime(worldData.getTime());
        worldInfo.setDaytime(worldData.isDaytime());
        return worldInfo;
    }

    public static String exportWorld(WorldInfo worldInfo) throws JsonProcessingException {
        WorldJSON json = new WorldJSON();
        json.setName(worldInfo.getName());
        json.setGravity(worldInfo.getGravity());
        json.setDifficulty(worldInfo.getDifficulty());
        json.setDays(worldInfo.getDays());
        json.setTime(worldInfo.getTime());
        json.setDaytime(worldInfo.isDaytime());
        return mapper.writeValueAsString(json);
    }

    public static void saveWorld(String json) throws IOException {
        WorldJSON worldJSON = mapper.readValue(json, WorldJSON.class);
        Files.write(Paths.get(String.format("%s/worlds/%s.json", pathName, worldJSON.getName())), json.getBytes());
    }

    public static WorldInfo[] scanWorlds() throws IOException {
        File folder = new File(pathName + "/worlds");
        File[] files = folder.listFiles();
        if (files == null) {
            return new WorldInfo[0];
        }
        List<WorldInfo> worldDataList = new ArrayList<>();
        List<File> jsonFiles = Arrays.stream(files).filter(file -> file.isFile() && file.getName().endsWith(".json")).toList();
        for (File jsonFile : jsonFiles) {
            try {
                WorldInfo w = importWorld(jsonFile);
                worldDataList.add(w);
            } catch (Exception e) {
                if (e instanceof JsonProcessingException) {
                    LOGGER.error("Invalid or corrupted world data: {}", jsonFile.getName(), e);
                } else {
                    throw e;
                }
            }

        }
        return worldDataList.toArray(new WorldInfo[0]);
    }

    public static Server importServer(File file) throws Exception {
        ServerJSON serverData = mapper.readValue(file, ServerJSON.class);
        return new Server(serverData.getName(), InetAddress.getByName(serverData.getAddress()), serverData.getPort());
    }

    public static String exportServer(Server server) throws JsonProcessingException {
        ServerJSON json = new ServerJSON();
        json.setName(server.getName());
        json.setAddress(server.getAddress().getHostName());
        json.setPort(server.getAddress().getPort());
        return mapper.writeValueAsString(json);
    }

    public static void saveServer(String json) throws IOException {
        ServerJSON serverJSON = mapper.readValue(json, ServerJSON.class);
        Files.write(Paths.get(String.format("%s/servers/%s.json", pathName, serverJSON.getName())), json.getBytes());
    }

    public static Server[] scanServers() throws Exception {
        File folder = new File(pathName + "/servers");
        File[] files = folder.listFiles();
        if (files == null) {
            return new Server[0];
        }
        List<Server> serverList = new ArrayList<>();
        List<File> jsonFiles = Arrays.stream(files).filter(file -> file.isFile() && file.getName().endsWith(".json")).toList();
        for (File jsonFile : jsonFiles) {
            try {
                Server e = importServer(jsonFile);
                serverList.add(e);
            } catch (Exception e) {
                if (e instanceof JsonProcessingException) {
                    LOGGER.error("Invalid or corrupted server data: {}", jsonFile.getName(), e);
                } else {
                    throw e;
                }
            }
        }
        return serverList.toArray(new Server[0]);
    }

    private GameLoader() {

    }
}
