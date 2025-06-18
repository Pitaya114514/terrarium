package com.pitaya.terrarium.client.render;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.client.GameLoader;
import com.pitaya.terrarium.client.render.resources.ResourceLoadingException;
import com.pitaya.terrarium.client.render.resources.ResourcePack;
import com.pitaya.terrarium.client.render.resources.SoundSource;
import com.pitaya.terrarium.game.LocalTerrarium;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import com.pitaya.terrarium.game.entity.life.player.PlayerMoveController;
import com.pitaya.terrarium.game.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.pitaya.terrarium.game.util.Util.Rendering.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class Renderer {
    public final static Logger LOGGER = LogManager.getLogger(Renderer.class);

    private ResourcePack resourcePack;
    private boolean isInDebugMode;
    private boolean shouldAutoAim;
    private Util.Counter fpsCounter = new Util.Counter();
    private Camara camara;
    private long window;
    private Thread renderThread;
    private LivingEntity targetEntity;

    //用于update()的缓存字段
    private final double[] xpos = new double[1];
    private final double[] ypos = new double[1];
    private final Vector2f cursorPos = new Vector2f();
    private final Vector2f windowSize = new Vector2f();

    float[] sky = Util.Rendering.getVertices(
            getVertex(-1.0f, 1.0f, new Color(3, 128, 190)),
            getVertex(1.0f, 1.0f, new Color(3, 128, 190)),
            getVertex(1.0f, -1.0f, new Color(240, 243, 241)),
            getVertex(-1.0f, -1.0f, new Color(240, 243, 241)));
    int[] skyi = {0, 1, 2, 3};
    int skyVao;

    int program;
    int textureProgram;
    int entityProgram;
    private int width;
    private int height;

    public void load() {
        Runnable r = () -> {
            init();
            fpsCounter = new Util.Counter();
            fpsCounter.schedule();
            while (!glfwWindowShouldClose(window)) {
                update();
                fpsCounter.addCount();
            }
            fpsCounter.cancel();
            closeWindow();
            resourcePack.free();
            try {
                GameLoader.savePlayer(GameLoader.exportPlayer(Main.getClient().player));
                LOGGER.info("Player data has been saved: {}", Main.getClient().player.entity().getName());
                if (Main.getClient().getTerrarium() instanceof LocalTerrarium lTerrarium) {
                    GameLoader.saveWorld(GameLoader.exportWorld(lTerrarium.getWorldInfo()));
                    LOGGER.info("World data has been saved: {}", lTerrarium.getWorldName());
                }
            } catch (IOException e) {
                LOGGER.error(e);
            }
        };
        if (renderThread == null || renderThread.getState() == Thread.State.TERMINATED) {
            renderThread = new Thread(r);
            renderThread.setName("RenderThread");
            renderThread.setDaemon(true);
            renderThread.start();
        } else if (renderThread.getState() == Thread.State.NEW) {
            renderThread.start();
        }
    }

    private void init() {
        //初始化OpenAL运行库
        initAl();
        //初始化GLFW运行库
        initGlfw();
        //初始化OpenGL运行库
        initGl();

        //设置窗口大小
        int[] w = new int[1];
        int[] h = new int[1];
        glfwGetWindowSize(window, w, h);
        windowSize.set(w[0], h[0]);
        camara = new Camara();
        camara.setPos(new Vector2f());
        reload(width, height);

        //加载资源
        try {
            resourcePack = new ResourcePack("res/core");
        } catch (ResourceLoadingException e) {
            LOGGER.error("Failed to load resource pack: ", e);
        }

        //GL配置
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA); 

        //加载来自资源包的所有着色器文本
        program = glCreateProgram();
        int vs = loadShader(resourcePack.shaderPack.vertex, GL_VERTEX_SHADER);
        int fs = loadShader(resourcePack.shaderPack.fragment, GL_FRAGMENT_SHADER);
        glAttachShader(program, vs);
        glAttachShader(program, fs);
        glLinkProgram(program);
        glDeleteShader(vs);
        glDeleteShader(fs);

        textureProgram = glCreateProgram();
        int tvs = loadShader(resourcePack.shaderPack.vertex_texture, GL_VERTEX_SHADER);
        int tfs = loadShader(resourcePack.shaderPack.fragment_texture, GL_FRAGMENT_SHADER);
        glAttachShader(textureProgram, tvs);
        glAttachShader(textureProgram, tfs);
        glLinkProgram(textureProgram);
        glDeleteShader(tvs);
        glDeleteShader(tfs);

        entityProgram = glCreateProgram();
        int evs = loadShader(resourcePack.shaderPack.entity_vertex, GL_VERTEX_SHADER);
        int efs = loadShader(resourcePack.shaderPack.entity_fragment, GL_FRAGMENT_SHADER);
        glAttachShader(entityProgram, evs);
        glAttachShader(entityProgram, efs);
        glLinkProgram(entityProgram);
        glDeleteShader(evs);
        glDeleteShader(efs);

        //创建顶点缓存对象
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();
        skyVao = glGenVertexArrays();
        glBindVertexArray(skyVao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, sky, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, skyi, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glUseProgram(entityProgram);
        glUniform2f(0, 0, 0);
        glUniform2f(1, width, height);

        //导入全局配置
        isInDebugMode = Boolean.parseBoolean(Main.getClient().properties.getProperty("debug-mode"));
        shouldAutoAim = Boolean.parseBoolean(Main.getClient().properties.getProperty("auto-aim"));

        //显示窗口
        glfwShowWindow(window);
    }

    private void initGlfw() {
        if (!glfwInit()) {
            LOGGER.error("Unable to initialize GLFW");
        } else {
            glfwSetErrorCallback((error, description) -> {
                LOGGER.error("{}, {}", error, description);
            });
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        }
        width = Integer.parseInt(Main.getClient().properties.getProperty("game-width"));
        height = Integer.parseInt(Main.getClient().properties.getProperty("game-height"));
        String title = isInDebugMode ? "Terrarium" :
                "TERRARIUM";
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new IllegalStateException("Unable to create game window");
        }
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
                        Main.getClient().player.entity().moveController.jump(200, Main.getClient().getTerrarium().getWorldInfo().getGravity());
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
                }
            }
        });
        glfwSetWindowSizeCallback(window, ((window1, width1, height1) ->  {
            windowSize.set(width1, height1);
            reload(width1, height1);
        }));
        glfwSetCharCallback(window, (window1, codepoint) -> {

        });
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
    }

    private void initAl() {
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        long device;
        if (defaultDeviceName != null) {
            device = alcOpenDevice(defaultDeviceName);
        } else {
            throw new RuntimeException();
        }
        long context = alcCreateContext(device, new int[]{0});
        alcMakeContextCurrent(context);
        AL.createCapabilities(ALC.createCapabilities(device));
    }

    private void initGl() {
        GL.createCapabilities();
    }

    private void update() {
        if (isInDebugMode) {
            glfwSetWindowTitle(window, String.format("Terrarium | fps=%s | tps=%s | entities=%s", getFps(), Main.getClient().getTerrarium().getTps(), Main.getClient().getTerrarium().getWorldInfo().getEntityList().size()));
        }

        glfwGetCursorPos(window, xpos, ypos);
        cursorPos.set(xpos[0], ypos[0]);
        Vector2f cursorPos2 = camara.getWorldPos(cursorPos);
        Main.getClient().player.cursorPos.set(cursorPos2);

        int leftButtonState = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT);
        if (leftButtonState == GLFW_PRESS) {
            Main.getClient().getTerrarium().useItem(Main.getClient().player.entity().getBackpack().getItem(Main.getClient().player.backpackIndex));
        }

        if (shouldAutoAim && targetEntity != null) {
            Main.getClient().player.entity().targetPos.set(targetEntity.position);
        } else {
            Main.getClient().player.entity().targetPos.set(cursorPos2);
        }

        if (Main.getClient().player.isMovingToLeft) {
            Main.getClient().player.entity().moveController.moveHorizontallyTo(false, 3);
        }
        if (Main.getClient().player.isMovingToRight) {
            Main.getClient().player.entity().moveController.moveHorizontallyTo(true, 3);
        }

        glClear(GL_COLOR_BUFFER_BIT);

        glUseProgram(program);
        render(skyVao, 4, GL_QUADS);
        glUseProgram(entityProgram);
        List<Entity> entityList = Main.getClient().getTerrarium().getWorldInfo().getEntityList();
        for (int i = 0; i < entityList.size(); i++) {
            Entity entity = entityList.get(i);
            int vbo = glGenBuffers();
            int ebo = glGenBuffers();
            int vao = glGenVertexArrays();
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            float[] vertices = getVertices(entity);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, new int[]{0, 1, 2, 3}, GL_DYNAMIC_DRAW);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 5 * Float.BYTES, 0);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 5 * Float.BYTES, 2 * Float.BYTES);
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            render(vao, 4, GL_QUADS);
        }
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    private static float[] getVertices(Entity entity) {
        Vector2f center = entity.box.center;
        float eHeight = entity.box.getHeight();
        float eWidth = entity.box.getWidth();
        return new  float[]{
                center.x - eWidth, center.y + eHeight, 0.015f, 0.9f, 0.015f,
                center.x + eWidth, center.y + eHeight, 0.015f, 0.9f, 0.015f,
                center.x + eWidth, center.y - eHeight, 0.015f, 0.9f, 0.015f,
                center.x - eWidth, center.y - eHeight, 0.765f, 0.9f, 0.015f
        };
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

    public void closeWindow() {
        glfwDestroyWindow(window);
    }

    private void playSound(SoundSource soundSource) {
        alSourcePlay(soundSource.source);
    }
}
