package org.terrarium.core.client;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWImage;
import org.terrarium.Main;
import org.terrarium.core.client.network.ClientCommunicator;
import org.terrarium.core.client.network.RemoteTerrarium;
import org.terrarium.core.client.network.Server;
import org.terrarium.core.client.resources.ResourceLoadingException;
import org.terrarium.core.client.resources.ResourcePack;
import org.terrarium.core.config.ConfigManager;
import org.terrarium.core.game.GameInitializer;
import org.terrarium.core.game.Terrarium;
import org.terrarium.core.game.LocalTerrarium;
import org.terrarium.core.game.entity.player.PlayerDifficulty;
import org.terrarium.core.game.network.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.terrarium.core.game.util.Util;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.MemoryUtil.NULL;

public class TerrariumClient {

    private static final Logger LOGGER = LogManager.getLogger(TerrariumClient.class);

    public final GameInitializer gameInitializer;
    public final ConfigManager configManager;
    public final Renderer renderer = new Renderer();
    public final AudioPlayer audioPlayer = new AudioPlayer();
    public final ActionController actionController = new ActionController();
    public final ClientCommunicator communicator = new ClientCommunicator();
    public final Set<Server> servers = new HashSet<>();
    private final double[] cursorX = new double[1];
    private final double[] cursorY = new double[1];
    private ResourcePack resourcePack;
    public Player player;
    private Util.Counter fpsCounter;
    private Terrarium terrarium;

    public TerrariumClient(ClientInitializer cInitializer, GameInitializer gInitializer) {
        LOGGER.info("Terrarium Client | {}", Main.VERSION);
        this.gameInitializer = gInitializer;
        this.configManager = new ConfigManager("client", "Client properties", cInitializer.init());
    }

    public void launch(String name, Vector2f spawnPosition, long seed) {
        LOGGER.info("Game arguments: \n  Name: {}\n  Seed: {}\n  SpawnPoint: {}", name, seed, spawnPosition);
        this.player = new Player(name, PlayerDifficulty.CLASSIC);
        runLocalTerrarium(player, gameInitializer, seed, spawnPosition);
        long window = createGameWindow();

        try {
            resourcePack = new ResourcePack("res/core");
            resourcePack.loadTextPack();
            resourcePack.loadShaderPack();

            renderer.load(window);
            audioPlayer.load(window);

            resourcePack.loadSoundPack();
            resourcePack.loadTexturePack();
        } catch (ResourceLoadingException e) {
            LOGGER.error("Failed to load resource pack: ", e);
        }

        fpsCounter = new Util.Counter();
        fpsCounter.schedule();
        glfwShowWindow(window);
        while (!glfwWindowShouldClose(window)) {
            tick(window);
            fpsCounter.addCount();
        }
        fpsCounter.cancel();
        glfwDestroyWindow(window);
        resourcePack.free();
        communicator.close();
        if (terrarium instanceof LocalTerrarium lTerrarium) {
            lTerrarium.stopWorld();
        }
    }

    private void tick(long window) {
        glfwGetCursorPos(window, cursorX, cursorY);
        player.cursorPos.set(renderer.getGamePosX(cursorX[0]), renderer.getGamePosY(cursorY[0]));
        renderer.tick(window, terrarium);
        actionController.tick(player, terrarium);
    }

    private void runLocalTerrarium(Player player, GameInitializer initializer, long seed, Vector2f spawnPosition) {
        this.terrarium = new LocalTerrarium(initializer);
        player.createEntity(terrarium);
        LOGGER.info("Loading world, Player: {}", player);
        LocalTerrarium lTerrarium = (LocalTerrarium) this.terrarium;
        lTerrarium.startWorld(seed);
        lTerrarium.addEntity(player.getEntity(), spawnPosition, player.name);
    }

    private void runRemoteTerrarium(Player player, Server server) throws CommunicationException {
        LOGGER.info("Connecting to {}", server);
        communicator.connect(server);
        this.terrarium = new RemoteTerrarium(communicator);
        player.createEntity(terrarium);
        LOGGER.info("Connecting to server: {}, Player: {}", server, player);
    }

    private long createGameWindow() {
        if (!glfwInit()) {
            LOGGER.error("Unable to initialize GLFW");
        } else {
            glfwSetErrorCallback((error, description) -> LOGGER.error("{}, {}", error, description));
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        }

        int width = Integer.parseInt(Main.getClient().configManager.find("client", "game-window-width"));
        int height = Integer.parseInt(Main.getClient().configManager.find("client", "game-window-height"));

        String title = "Terrarium " + Main.VERSION;
        long window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            return window;
        }

        glfwSetKeyCallback(window, (window1, key, scancode, action, mods) -> {
            final boolean isClicking = action != GLFW_RELEASE;
            switch (key) {
                case GLFW_KEY_SPACE -> actionController.spaceKey = isClicking;

                case GLFW_KEY_D -> actionController.rightKey = isClicking;

                case GLFW_KEY_A -> actionController.leftKey = isClicking;

                case GLFW_KEY_MINUS -> renderer.zoomInKey = isClicking;

                case GLFW_KEY_EQUAL -> renderer.zoomOutKey = isClicking;
            }
        });
        glfwSetMouseButtonCallback(window, ((window1, button, action, mods) -> {
            final boolean isClicking = action != GLFW_RELEASE;
            switch (button) {
                case GLFW_MOUSE_BUTTON_LEFT -> actionController.attack = isClicking;

                case GLFW_MOUSE_BUTTON_RIGHT -> actionController.use = isClicking;
            }
        }));

        return window;
    }

    public Terrarium getTerrarium() {
        return terrarium;
    }

    public ResourcePack getResourcePack() {
        return resourcePack;
    }

    public int getFps() {
        if (fpsCounter == null) {
            return 0;
        }
        return fpsCounter.getValue();
    }

}
