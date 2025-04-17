package com.pitaya.terrarium.client.render;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.game.entity.Entity;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.util.HashSet;

public final class Renderer implements Runnable {
    static {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        } else {
            GLFW.glfwDefaultWindowHints();
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        }
    }
    public static final int FRAME = 1000 / 60;
    public double fps;

    private Camara camara;
    private Hud hud;
    private long window;

    @Override
    public void run() {
        init();
        while (!GLFW.glfwWindowShouldClose(window)) {
            update();
            GLFW.glfwPollEvents();
            GLFW.glfwSwapBuffers(window);
            try {
                Thread.sleep(FRAME);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        GLFW.glfwDestroyWindow(window);
        Main.getClient().terminateTerrarium();
    }

    @SuppressWarnings("unchecked")
    private void update() {
        GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);
        camara.pos.set(Main.getClient().player.entity().position);
        if (Main.getClient().player.isMovingToLeft) {
            Main.getClient().player.entity().moveController.moveHorizontallyTo(false, 3);
        }
        if (Main.getClient().player.isMovingToRight) {
            Main.getClient().player.entity().moveController.moveHorizontallyTo(true, 3);
        }

        if (hud.isEnable()) {
            for (HudItem hudItem : hud.getHudItems()) {
                hudItem.render();
            }
        }
         Vector2f pos1 = new Vector2f(-999, 0);
        Vector2f pos2 = new Vector2f(999, 0);
        camara.render(pos1, pos2, null, null, GL33.GL_LINES, 0.6f, 1.0f, 0.8f);
        for (Entity entity : Main.getClient().terrarium.getEntitySet()) {
            camara.render(entity.box.getTopLeft(), entity.box.getBottomLeft(), entity.box.getBottomRight(), entity.box.getTopRight(), GL33.GL_QUADS, 0.6f, 0.7f, 0.8f);
        }
    }

    private void init() {
        int width = Integer.parseInt(Main.getClient().properties.getProperty("game-width"));
        int height = Integer.parseInt(Main.getClient().properties.getProperty("game-height"));
        window = GLFW.glfwCreateWindow(width, height, "Terrarium", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new IllegalStateException("Unable to create game window");
        }
        GLFWMouseButtonCallback mouseButtonCallback = GLFW.glfwSetMouseButtonCallback(window, (window1, button, action, mods) -> {
            switch (action) {

            }
        });
        GLFWKeyCallback keyCallback = GLFW.glfwSetKeyCallback(window, (window1, key, scancode, action, mods) -> {
            switch (key) {
                case GLFW.GLFW_KEY_A ->
                        Main.getClient().player.isMovingToLeft = action != GLFW.GLFW_RELEASE && !Main.getClient().player.isMovingToRight;
                case GLFW.GLFW_KEY_D ->
                        Main.getClient().player.isMovingToRight = action != GLFW.GLFW_RELEASE && !Main.getClient().player.isMovingToLeft;
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
            }
        });
        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GL.createCapabilities();
        GL33.glMatrixMode(GL33.GL_PROJECTION);
        GL33.glLoadIdentity();
        GL33.glOrtho(0, 800, 600, 0, -1, 1);
        GL33.glMatrixMode(GL33.GL_MODELVIEW);
        GL33.glLoadIdentity();
        hud = new Hud(Hud.getDefaultHudItems());
        camara = new Camara();
        camara.setHeight(height);
        camara.pos.set(0, 0);
        GLFW.glfwShowWindow(window);
    }
}
