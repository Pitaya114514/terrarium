package com.pitaya.terrarium.client.render;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.barrage.Shell;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.boss.BossEntity;
import com.pitaya.terrarium.game.tool.Counter;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

public final class Renderer {
    static {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        } else {
            GLFW.glfwDefaultWindowHints();
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        }
    }

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
        GLFW.glfwSetWindowTitle(window, String.format("Terrarium | fps=%s", getFps()));
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
            hud.render(camara.getRenderPos(Main.getClient().player.entity().position), cursorPos, null, null, GL33.GL_LINES, 0.3f, 1.0f, 0.0f);
        }

        Vector2f pos1 = new Vector2f(-999, 0);
        Vector2f pos2 = new Vector2f(999, 0);
        camara.render(pos1, pos2, null, null, GL33.GL_LINES, 0.6f, 1.0f, 0.8f);
        for (Entity entity : Main.getClient().terrarium.getEntitySet()) {
            camara.render(entity.box.getTopLeft(), entity.box.getBottomLeft(), entity.box.getBottomRight(), entity.box.getTopRight(), GL33.GL_QUADS, 0.6f, 0.7f, 0.8f);
            if (entity instanceof BossEntity) {
                ((BossHealthBar) Hud.HudItems.BOSS_HEALTH_BAR.getRenderModule()).setTargetEntity((LivingEntity) entity);
            }
        }

    }

    private void init() {
        int width = Integer.parseInt(Main.getClient().properties.getProperty("game-width"));
        int height = Integer.parseInt(Main.getClient().properties.getProperty("game-height"));
        window = GLFW.glfwCreateWindow(width, height, "Terrarium", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new IllegalStateException("Unable to create game window");
        }
        GLFW.glfwSetMouseButtonCallback(window, (window1, button, action, mods) -> {
            switch (action) {
                case GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                    Main.getClient().terrarium.addEntity(new Shell(Main.getClient().player.entity().position, Main.getClient().player.cursorPos));
                }
            }
        });
        GLFW.glfwSetKeyCallback(window, (window1, key, scancode, action, mods) -> {
            switch (key) {
                case GLFW.GLFW_KEY_A -> {
                    if (Hud.HudItems.CHAT_BAR.getRenderModule().isEnable()) {
                        ((CharBar) Hud.HudItems.CHAT_BAR.getRenderModule()).input("a");
                    } else {
                        Main.getClient().player.isMovingToLeft = action != GLFW.GLFW_RELEASE && !Main.getClient().player.isMovingToRight;
                    }
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
            }
        });
        GLFW.glfwSetWindowSizeCallback(window, ((window1, width1, height1) ->  {
            windowSize.set(width1, height1);
            reload(width1, height1);
        }));
        GLFW.glfwSetCursorPosCallback(window, (window1, xpos, ypos) -> {
            cursorPos.set(xpos, ypos);
            Main.getClient().player.cursorPos.set(camara.getWorldPos(cursorPos));
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
