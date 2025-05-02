package com.pitaya.terrarium.client.render;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.client.TerrariumClient;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.mob.boss.BossEntity;
import com.pitaya.terrarium.game.tool.Counter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.util.ConcurrentModificationException;
import java.util.concurrent.ThreadLocalRandom;

public final class Renderer {
    public final static Logger LOGGER = LogManager.getLogger(Renderer.class);
    static {
        if (!GLFW.glfwInit()) {
            LOGGER.warn("Unable to initialize GLFW");
        } else {
            GLFW.glfwDefaultWindowHints();
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        }
    }

    private boolean isInDebugMode;
    private Counter fpsCounter = new Counter();
    private Camara camara;
    private Hud hud;
    private long window;
    private final Vector2f cursorPos = new Vector2f();
    private final Vector2f windowSize = new Vector2f();
    private Thread renderThread;

    public void load() {
        Runnable renderFunction = () -> {
            init();
            fpsCounter = new Counter();
            fpsCounter.schedule();
            while (!GLFW.glfwWindowShouldClose(window)) {
                update();
                fpsCounter.addCount();
                GLFW.glfwPollEvents();
                GLFW.glfwSwapBuffers(window);
            }
            fpsCounter.cancel();
            GLFW.glfwDestroyWindow(window);
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
            GLFW.glfwSetWindowTitle(window, String.format("Terrarium | fps=%s | tps=%s", getFps(), Main.getClient().terrarium.getTps()));
        }

        double[] xpos = new double[1];
        double[] ypos = new double[1];
        GLFW.glfwGetCursorPos(window, xpos, ypos);
        cursorPos.set(xpos[0], ypos[0]);
        Vector2f cursorPos2 = camara.getWorldPos(cursorPos);
        Main.getClient().player.cursorPos.set(cursorPos2);
        Main.getClient().player.entity().targetPos.set(cursorPos2);

        GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);
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
        camara.render(pos1, pos2, null, null, GL33.GL_LINES, 0.6f, 1.0f, 0.8f);

        for (int i = 0; i < Main.getClient().terrarium.getEntityList().size(); i++) {
            Entity entity = Main.getClient().terrarium.getEntityList().get(i);
            camara.render(entity.box.getTopLeft(), entity.box.getBottomLeft(), entity.box.getBottomRight(), entity.box.getTopRight(), GL33.GL_QUADS, 0.6f, 0.7f, 0.8f);
            if (entity instanceof BossEntity) {
                ((BossHealthBar) Hud.HudItems.BOSS_HEALTH_BAR.getRenderModule()).setTargetEntity((LivingEntity) entity);
            }
        }
    }

    private void init() {
        isInDebugMode = Boolean.parseBoolean(Main.getClient().properties.getProperty("debug-mode"));
        int width = Integer.parseInt(Main.getClient().properties.getProperty("game-width"));
        int height = Integer.parseInt(Main.getClient().properties.getProperty("game-height"));
        String title = isInDebugMode ? "Terrarium" :
                TerrariumClient.TITLE_LIST.get(ThreadLocalRandom.current().nextInt(TerrariumClient.TITLE_LIST.size()));
        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new IllegalStateException("Unable to create game window");
        }
        GLFW.glfwSetMouseButtonCallback(window, (window1, button, action, mods) -> {
            if (action != GLFW.GLFW_RELEASE) {
                switch (button) {
                    case GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                        Main.getClient().terrarium.useItem(Main.getClient().player.entity().getBackpack().getItem(Main.getClient().player.backpackIndex));
                    }
                }
            }
        });
        GLFW.glfwSetKeyCallback(window, (window1, key, scancode, action, mods) -> {
            switch (key) {
                case GLFW.GLFW_KEY_A -> {
                    Main.getClient().player.isMovingToLeft = action != GLFW.GLFW_RELEASE && !Main.getClient().player.isMovingToRight;
                }

                case GLFW.GLFW_KEY_D -> {
                    Main.getClient().player.isMovingToRight = action != GLFW.GLFW_RELEASE && !Main.getClient().player.isMovingToLeft;
                }

                case GLFW.GLFW_KEY_SPACE -> {
                    if (action != GLFW.GLFW_RELEASE) {
                        Main.getClient().player.entity().moveController.jump(200);
                    }
                }

                case GLFW.GLFW_KEY_F11 -> {
                    if (action != GLFW.GLFW_RELEASE) {
                        hud.setEnable(!hud.isEnable());
                    }
                }

                case GLFW.GLFW_KEY_ENTER -> {
                    if (action != GLFW.GLFW_RELEASE) {
                        CharBar charBar = (CharBar) Hud.HudItems.CHAT_BAR.getRenderModule();
                        if (charBar.isEnable()) {
                            Main.getClient().terrarium.sendMassage(charBar.toString());
                            charBar.clear();
                        }
                        charBar.setEnable(!charBar.isEnable());
                    }
                }

                case GLFW.GLFW_KEY_H -> {
                    Main.getClient().player.entity().setHealth(Main.getClient().player.entity().getHealth() + 50);
                }
            }
            if (action != GLFW.GLFW_RELEASE) {
                switch (key) {
                    case GLFW.GLFW_KEY_1 -> {
                        Main.getClient().player.backpackIndex = 0;
                        LOGGER.info(Main.getClient().player.entity().getBackpack().getItem(Main.getClient().player.backpackIndex));
                    }

                    case GLFW.GLFW_KEY_2 -> {
                        Main.getClient().player.backpackIndex = 1;
                        LOGGER.info(Main.getClient().player.entity().getBackpack().getItem(Main.getClient().player.backpackIndex));
                    }

                    case GLFW.GLFW_KEY_3 -> {
                        Main.getClient().player.backpackIndex = 2;
                        LOGGER.info(Main.getClient().player.entity().getBackpack().getItem(Main.getClient().player.backpackIndex));
                    }

                    case GLFW.GLFW_KEY_4 -> {
                        Main.getClient().player.backpackIndex = 3;
                        LOGGER.info(Main.getClient().player.entity().getBackpack().getItem(Main.getClient().player.backpackIndex));
                    }

                    case GLFW.GLFW_KEY_5 -> {
                        Main.getClient().player.backpackIndex = 4;
                        LOGGER.info(Main.getClient().player.entity().getBackpack().getItem(Main.getClient().player.backpackIndex));
                    }

                    case GLFW.GLFW_KEY_6 -> {
                        Main.getClient().player.backpackIndex = 5;
                        LOGGER.info(Main.getClient().player.entity().getBackpack().getItem(Main.getClient().player.backpackIndex));
                    }

                    case GLFW.GLFW_KEY_7 -> {
                        Main.getClient().player.backpackIndex = 6;
                        LOGGER.info(Main.getClient().player.entity().getBackpack().getItem(Main.getClient().player.backpackIndex));
                    }

                    case GLFW.GLFW_KEY_8 -> {
                        Main.getClient().player.backpackIndex = 7;
                        LOGGER.info(Main.getClient().player.entity().getBackpack().getItem(Main.getClient().player.backpackIndex));
                    }

                    case GLFW.GLFW_KEY_9 -> {
                        Main.getClient().player.backpackIndex = 8;
                        LOGGER.info(Main.getClient().player.entity().getBackpack().getItem(Main.getClient().player.backpackIndex));
                    }
                }
            }
        });
        GLFW.glfwSetWindowSizeCallback(window, ((window1, width1, height1) ->  {
            windowSize.set(width1, height1);
            reload(width1, height1);
        }));
        GLFW.glfwSetCursorPosCallback(window, (window1, xpos, ypos) -> {
        });
        GLFW.glfwSetCharCallback(window, (window1, codepoint) -> {
            if (Hud.HudItems.CHAT_BAR.getRenderModule().isEnable()) {
                ((CharBar) Hud.HudItems.CHAT_BAR.getRenderModule()).input((char) codepoint);
            }
        });
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GL.createCapabilities();
        int[] w = new int[1];
        int[] h = new int[1];
        GLFW.glfwGetWindowSize(window, w, h);
        windowSize.set(w[0], h[0]);
        hud = new Hud();
        hud.setEnable(true);
        camara = new Camara();
        camara.setPos(new Vector2f());
        reload(width, height);
        GLFW.glfwShowWindow(window);
    }

    public void reload(int width, int height) {
        GL33.glMatrixMode(GL33.GL_PROJECTION);
        GL33.glLoadIdentity();
        GL33.glOrtho(0, width, height, 0, -1, 1);
        GL33.glMatrixMode(GL33.GL_MODELVIEW);
        GL33.glLoadIdentity();
        camara.setHeight(height);
    }

    public int getFps() {
        if (fpsCounter == null) {
            return 0;
        }
        return fpsCounter.getValue();
    }
}
