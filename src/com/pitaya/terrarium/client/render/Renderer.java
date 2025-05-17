package com.pitaya.terrarium.client.render;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.client.GameLoader;
import com.pitaya.terrarium.client.TerrariumClient;
import com.pitaya.terrarium.client.render.resources.ResourceLoadingException;
import com.pitaya.terrarium.client.render.resources.ResourcePack;
import com.pitaya.terrarium.client.render.resources.Sound;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.player.PlayerMoveController;
import com.pitaya.terrarium.game.entity.life.mob.boss.BossEntity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.util.Counter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class Renderer {
    public final static Logger LOGGER = LogManager.getLogger(Renderer.class);

    private ResourcePack resourcePack;
    private boolean isInDebugMode;
    private boolean shouldAutoAim;
    private Counter fpsCounter = new Counter();
    private Camara camara;
    private Hud hud;
    private long window;
    private long monitor;
    private Thread renderThread;
    private LivingEntity targetEntity;
    private boolean isRunning;

    private final Vector2f cursorPos = new Vector2f();
    private final Vector2f windowSize = new Vector2f();
    private final Vector2f tlPos = new Vector2f();
    private final Vector2f blPos = new Vector2f();
    private final Vector2f brPos = new Vector2f();
    private final Vector2f trPos = new Vector2f();

    public void load() {
        Runnable renderFunction = () -> {
            init();
            playSound(resourcePack.soundPack.roar_1);
            fpsCounter = new Counter();
            fpsCounter.schedule();
            isRunning = true;
            while (!glfwWindowShouldClose(window)) {
                update();
                fpsCounter.addCount();
                glfwPollEvents();
                glfwSwapBuffers(window);
            }
            fpsCounter.cancel();
            isRunning = false;
            closeWindow();
            try {
                GameLoader gameLoader = Main.getClient().gameLoader;
                gameLoader.savePlayer(gameLoader.exportPlayer(Main.getClient().player));
                LOGGER.info("Player data has been saved: {}", Main.getClient().player.entity().name);
                gameLoader.saveWorld(Main.getClient().terrarium.exportWorldData());
                LOGGER.info("World data has been saved: {}", Main.getClient().terrarium.getWorldName());
            } catch (IOException e) {
                LOGGER.error(e);
            }
            Main.getClient().terminateTerrarium();
        };
        if (renderThread == null || renderThread.getState() == Thread.State.TERMINATED) {
            renderThread = new Thread(renderFunction);
            renderThread.setName("RenderThread");
            renderThread.setDaemon(true);
            renderThread.start();
        } else if (renderThread.getState() == Thread.State.NEW) {
            renderThread.start();
        }
    }

    private void update() {
        if (isInDebugMode) {
            glfwSetWindowTitle(window, String.format("Terrarium | fps=%s | tps=%s", getFps(), Main.getClient().terrarium.getTps()));
        }

        double[] xpos = new double[1];
        double[] ypos = new double[1];
        glfwGetCursorPos(window, xpos, ypos);
        cursorPos.set(xpos[0], ypos[0]);
        Vector2f cursorPos2 = camara.getWorldPos(cursorPos);
        Main.getClient().player.cursorPos.set(cursorPos2);
        if (shouldAutoAim && targetEntity != null) {
            Main.getClient().player.entity().targetPos.set(targetEntity.position);
        } else {
            Main.getClient().player.entity().targetPos.set(cursorPos2);
        }

        glClear(GL_COLOR_BUFFER_BIT);

        camara.setPos(Main.getClient().player.entity().position);
        if (Main.getClient().player.isMovingToLeft) {
            Main.getClient().player.entity().moveController.moveHorizontallyTo(false, 3);
        }
        if (Main.getClient().player.isMovingToRight) {
            Main.getClient().player.entity().moveController.moveHorizontallyTo(true, 3);
        }

        if (hud.isEnable()) {
            hud.render(Hud.HudItems.CHAT_BAR.getRenderModule(), windowSize);
            hud.render(Hud.HudItems.PLAYER_HEALTH_BAR.getRenderModule(), windowSize);
            hud.render(Hud.HudItems.BOSS_HEALTH_BAR.getRenderModule(), windowSize);
        }

        Vector2f pos1 = new Vector2f(Integer.MIN_VALUE, 0);
        Vector2f pos2 = new Vector2f(Integer.MAX_VALUE, 0);

        camara.render(pos1, pos2, null, null, GL_LINES, 0.6f, 1.0f, 0.8f);

        for (int i = 0; i < Main.getClient().terrarium.getEntityList().size(); i++) {
            Entity entity = Main.getClient().terrarium.getEntityList().get(i);

            if (entity instanceof LivingEntity livingEntity && livingEntity.healthManager.isWounded) {
                playSound(resourcePack.soundPack.hit_1);
                livingEntity.healthManager.isWounded = false;
            }

            if (shouldAutoAim && entity != Main.getClient().player.entity() && entity instanceof LivingEntity livingEntity) {
                if (!livingEntity.healthManager.isInvincible && livingEntity.isAlive()) {
                    targetEntity = livingEntity;
                }
            }

            if (entity instanceof BossEntity) {
                ((BossHealthBar) Hud.HudItems.BOSS_HEALTH_BAR.getRenderModule()).setTargetEntity((LivingEntity) entity);
            }

            float[] rgb = new float[3];
            if (entity instanceof LivingEntity livingEntity && livingEntity.healthManager.isInvincible) {
                rgb[0] = 0.4f;
                rgb[1] = 0.5f;
                rgb[2] = 0.6f;
            } else {
                rgb[0] = 0.6f;
                rgb[1] = 0.7f;
                rgb[2] = 0.8f;
            }

            camara.render(entity.box.getTopLeft(tlPos), entity.box.getBottomLeft(blPos), null, null, GL_LINES, rgb[0], rgb[1], rgb[2]);
            camara.render(entity.box.getBottomLeft(blPos), entity.box.getBottomRight(brPos), null, null, GL_LINES, rgb[0], rgb[1], rgb[2]);
            camara.render(entity.box.getBottomRight(brPos), entity.box.getTopRight(trPos), null, null, GL_LINES, rgb[0], rgb[1], rgb[2]);
            camara.render(trPos, tlPos, null, null, GL_LINES, rgb[0], rgb[1], rgb[2]);
        }

    }

    private void init() {
        initGl();
        initAl();
        try {
            resourcePack = new ResourcePack("res/terrarium_resource_pack");
        } catch (ResourceLoadingException e) {
            LOGGER.error(e);
        }
    }

    private void initGl() {
        if (!glfwInit()) {
            LOGGER.warn("Unable to initialize GLFW");
        } else {
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        }
        isInDebugMode = Boolean.parseBoolean(Main.getClient().properties.getProperty("debug-mode"));
        shouldAutoAim = Boolean.parseBoolean(Main.getClient().properties.getProperty("auto-aim"));
        int width = Integer.parseInt(Main.getClient().properties.getProperty("game-width"));
        int height = Integer.parseInt(Main.getClient().properties.getProperty("game-height"));
        String title = isInDebugMode ? "Terrarium" :
                TerrariumClient.TITLE_LIST.get(ThreadLocalRandom.current().nextInt(TerrariumClient.TITLE_LIST.size()));
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        monitor = glfwGetPrimaryMonitor();
        if (window == NULL) {
            throw new IllegalStateException("Unable to create game window");
        }
        glfwSetMouseButtonCallback(window, (window1, button, action, mods) -> {
            if (action != GLFW_RELEASE) {
                if (button == GLFW_MOUSE_BUTTON_LEFT) {
                    Main.getClient().terrarium.useItem(Main.getClient().player.entity().getBackpack().getItem(Main.getClient().player.backpackIndex));
                }
            }
        });
        glfwSetKeyCallback(window, (window1, key, scancode, action, mods) -> {
            switch (key) {
                case GLFW_KEY_A -> Main.getClient().player.isMovingToLeft = action != GLFW_RELEASE && !Main.getClient().player.isMovingToRight;

                case GLFW_KEY_D -> Main.getClient().player.isMovingToRight = action != GLFW_RELEASE && !Main.getClient().player.isMovingToLeft;

                case GLFW_KEY_W -> {
                    if (Main.getClient().player.entity().moveController instanceof PlayerMoveController pmc) {
                        pmc.isPressingW = action != GLFW_RELEASE && !pmc.isPressingS;
                    }
                }

                case GLFW_KEY_S -> {
                    if (Main.getClient().player.entity().moveController instanceof PlayerMoveController pmc) {
                        pmc.isPressingS = action != GLFW_RELEASE && !pmc.isPressingW;
                    }
                }

                case GLFW_KEY_SPACE -> {
                    if (action != GLFW_RELEASE) {
                        Main.getClient().player.entity().moveController.jump(200);
                    }
                }

                case GLFW_KEY_F11 -> {
                    if (action != GLFW_RELEASE) {
                        hud.setEnable(!hud.isEnable());
                    }
                }

                case GLFW_KEY_F12 -> {
                    if (action == GLFW_PRESS) {
                        if (glfwGetWindowMonitor(window) == 0) {
                            GLFWVidMode glfwVidMode = glfwGetVideoMode(monitor);
                            int width1 = glfwVidMode.width();
                            int height1 = glfwVidMode.height();
                            glfwSetWindowMonitor(window, monitor, 0, 0, width1, height1, glfwVidMode.refreshRate());
                            reload(width1, height1);
                        } else {
                            glfwSetWindowMonitor(window, 0, 100, 100, 800, 600, 0);
                        }
                    }
                }

                case GLFW_KEY_ENTER -> {
                    if (action != GLFW_RELEASE) {
                        CharBar charBar = (CharBar) Hud.HudItems.CHAT_BAR.getRenderModule();
                        if (charBar.isEnable() && !charBar.toString().isEmpty()) {
                            Main.getClient().terrarium.sendMassage(charBar.toString());
                            charBar.clear();
                        }
                        charBar.setEnable(!charBar.isEnable());
                    }
                }

                case GLFW_KEY_H -> Main.getClient().player.entity().setHealth(Main.getClient().player.entity().getHealth() + 50);
            }
            if (action != GLFW_RELEASE) {
                switch (key) {
                    case GLFW_KEY_1 -> Main.getClient().player.backpackIndex = 0;

                    case GLFW_KEY_2 -> Main.getClient().player.backpackIndex = 1;

                    case GLFW_KEY_3 -> Main.getClient().player.backpackIndex = 2;

                    case GLFW_KEY_4 -> Main.getClient().player.backpackIndex = 3;

                    case GLFW_KEY_5 -> Main.getClient().player.backpackIndex = 4;

                    case GLFW_KEY_6 -> Main.getClient().player.backpackIndex = 5;

                    case GLFW_KEY_7 -> Main.getClient().player.backpackIndex = 6;

                    case GLFW_KEY_8 -> Main.getClient().player.backpackIndex = 7;

                    case GLFW_KEY_9 -> Main.getClient().player.backpackIndex = 8;

                    case GLFW_KEY_0 -> {
                        Item item = Main.getClient().player.entity().getBackpack().getItem(Main.getClient().player.backpackIndex);
                        if (item != null) {
                            LOGGER.info(item.name);
                        }
                    }
                }
            }
        });
        glfwSetWindowSizeCallback(window, ((window1, width1, height1) ->  {
            windowSize.set(width1, height1);
            reload(width1, height1);
        }));
        glfwSetCharCallback(window, (window1, codepoint) -> {
            if (Hud.HudItems.CHAT_BAR.getRenderModule().isEnable()) {
                ((CharBar) Hud.HudItems.CHAT_BAR.getRenderModule()).input((char) codepoint);
            }
        });
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        GL.createCapabilities();
        int[] w = new int[1];
        int[] h = new int[1];
        glfwGetWindowSize(window, w, h);
        windowSize.set(w[0], h[0]);
        hud = new Hud();
        hud.setEnable(true);
        camara = new Camara();
        camara.setPos(new Vector2f());
        reload(width, height);
        glfwShowWindow(window);
    }

    private void initAl() {
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        long device;
        if (defaultDeviceName != null) {
            device = alcOpenDevice(defaultDeviceName);
        } else {
            throw new RuntimeException();
        }
        int[] attrList = {0};
        long context = alcCreateContext(device, attrList);
        alcMakeContextCurrent(context);
        AL.createCapabilities(ALC.createCapabilities(device));
    }

    public void reload(int width, int height) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        camara.setHeight(height);
    }

    public int getFps() {
        if (fpsCounter == null) {
            return 0;
        }
        return fpsCounter.getValue();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void closeWindow() {
        glfwDestroyWindow(window);
    }

    private void playSound(Sound sound) {
        alSourcePlay(sound.source);
    }
}
