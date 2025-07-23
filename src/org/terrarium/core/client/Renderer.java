package org.terrarium.core.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.terrarium.Main;
import org.terrarium.core.client.resources.ShaderPack;
import org.terrarium.core.game.Terrarium;
import org.terrarium.core.game.block.Block;
import org.terrarium.core.game.entity.Box;
import org.terrarium.core.game.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL;
import org.terrarium.core.game.world.Chunk;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public final class Renderer {
    public final static Logger LOGGER = LogManager.getLogger(Renderer.class);

    private final FloatBuffer blockRenderingBuffer = FloatBuffer.allocate(5 * 4);
    private final FloatBuffer entityRenderingBuffer = FloatBuffer.allocate(5 * 4);
    private Camara camara;
    private float zoomMultiplier = 1;
    private final Vector2f windowSize = new Vector2f();
    private final LoadingCache<Entity, int[]> entityRenderingCache = CacheBuilder.newBuilder()
            .maximumSize(500)
            .expireAfterAccess(12, TimeUnit.SECONDS)
            .recordStats()
            .build(new CacheLoader<>() {
                @Override
                public int[] load(Entity entity) {
                    int vbo = glGenBuffers();
                    int ebo = glGenBuffers();
                    int vao = glGenVertexArrays();
                    glBindVertexArray(vao);
                    glBindBuffer(GL_ARRAY_BUFFER, vbo);
                    float[] vertices = getEntityVertices(entityRenderingBuffer, entity).array();
                    glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
                    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
                    glBufferData(GL_ELEMENT_ARRAY_BUFFER, new int[]{0, 1, 2, 3}, GL_DYNAMIC_DRAW);
                    glVertexAttribPointer(0, 2, GL_FLOAT, false, 5 * Float.BYTES, 0);
                    glVertexAttribPointer(1, 3, GL_FLOAT, false, 5 * Float.BYTES, 2 * Float.BYTES);
                    glEnableVertexAttribArray(0);
                    glEnableVertexAttribArray(1);
                    return new int[]{vao, vbo, ebo};
                }
            });
    private final LoadingCache<Block, int[]> blockRenderingCache = CacheBuilder.newBuilder()
            .expireAfterAccess(40, TimeUnit.SECONDS)
            .recordStats()
            .build(new CacheLoader<>() {
                @Override
                public int[] load(Block block) {
                    int vbo = glGenBuffers();
                    int ebo = glGenBuffers();
                    int vao = glGenVertexArrays();
                    glBindVertexArray(vao);
                    glBindBuffer(GL_ARRAY_BUFFER, vbo);
                    float[] vertices = getBlockVertices(blockRenderingBuffer, block).array();
                    glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
                    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
                    glBufferData(GL_ELEMENT_ARRAY_BUFFER, new int[]{0, 1, 2, 3}, GL_STATIC_DRAW);
                    glVertexAttribPointer(0, 2, GL_FLOAT, false, 5 * Float.BYTES, 0);
                    glVertexAttribPointer(1, 3, GL_FLOAT, false, 5 * Float.BYTES, 2 * Float.BYTES);
                    glEnableVertexAttribArray(0);
                    glEnableVertexAttribArray(1);
                    return new int[]{vao, vbo, ebo};
                }
            });

    float[] sky = getVertices(
            getVertex(-1.0f, 1.0f, new Color(17, 170, 229)),
            getVertex(1.0f, 1.0f, new Color(6, 48, 190)),
            getVertex(1.0f, -1.0f, new Color(255, 255, 255)),
            getVertex(-1.0f, -1.0f, new Color(255, 255, 255)));
    int[] skyi = {0, 1, 2, 3};
    int skyVao;

    int program;
    int textureProgram;
    int entityProgram;

    public boolean zoomInKey;
    public boolean zoomOutKey;

    private final float[] dirtColor = getColorVertex(new Color(193, 114, 33));
    private final float[] stoneColor = getColorVertex(new Color(130, 130, 130));
    private final float[] grassColor = getColorVertex(new Color(101, 222, 81));

    public void load(long window) {
        if (!glfwInit()) {
            LOGGER.error("Unable to initialize GLFW");
        } else {
            glfwSetErrorCallback((error, description) -> LOGGER.error("{}, {}", error, description));
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        //初始化OpenGL运行库
        GL.createCapabilities();

        //设置窗口大小
        int[] w = new int[1];
        int[] h = new int[1];
        glfwGetWindowSize(window, w, h);
        windowSize.set(w[0], h[0]);
        camara = new Camara();
        camara.setPos(new Vector2f());

        //GL配置
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //加载来自资源包的所有着色器文本
        program = glCreateProgram();
        ShaderPack shaderPack = Main.getClient().getResourcePack().getShaderPack();
        int vs = loadShader(shaderPack.vertex, GL_VERTEX_SHADER);
        int fs = loadShader(shaderPack.fragment, GL_FRAGMENT_SHADER);
        glAttachShader(program, vs);
        glAttachShader(program, fs);
        glLinkProgram(program);
        glDeleteShader(vs);
        glDeleteShader(fs);

        textureProgram = glCreateProgram();
        int tvs = loadShader(shaderPack.vertex_texture, GL_VERTEX_SHADER);
        int tfs = loadShader(shaderPack.fragment_texture, GL_FRAGMENT_SHADER);
        glAttachShader(textureProgram, tvs);
        glAttachShader(textureProgram, tfs);
        glLinkProgram(textureProgram);
        glDeleteShader(tvs);
        glDeleteShader(tfs);

        entityProgram = glCreateProgram();
        int evs = loadShader(shaderPack.entity_vertex, GL_VERTEX_SHADER);
        int efs = loadShader(shaderPack.entity_fragment, GL_FRAGMENT_SHADER);
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
        glUniform2f(1, windowSize.x, windowSize.y);
        glUniform1f(2, zoomMultiplier);
    }

    void tick(long window, Terrarium terrarium) {
        Player player = Main.getClient().player;
        camara.setPos(player.getEntity().getPosition());
        glClear(GL_COLOR_BUFFER_BIT);
        glUseProgram(program);
        render(skyVao, 4, GL_QUADS);
        glUseProgram(entityProgram);
        glUniform2f(0, camara.pos.x, camara.pos.y);
        List<Entity> entityList = terrarium.getEntityList();
        try {
            for (Map.Entry<Entity, Chunk[]> entry : terrarium.getChunkMap().entrySet()) {
                if (entry.getKey().getName().equals(player.name)) {
                    for (Chunk chunk : entry.getValue()) {
                        if (chunk == null) {
                            break;
                        }
                        for (int i = 0; i < chunk.blockSquare.length; i++) {
                            for (int j = 0; j < chunk.blockSquare.length; j++) {
                                Block block = chunk.blockSquare[i][j];
                                if (block != null) {
                                    int[] chunkRenderingObject = blockRenderingCache.get(block);
                                    glBindBuffer(GL_ARRAY_BUFFER, chunkRenderingObject[1]);
                                    glBufferSubData(GL_ARRAY_BUFFER, 0, getBlockVertices(blockRenderingBuffer, block).array());
                                    render(chunkRenderingObject[0], 4, GL_QUADS);
                                }
                            }
                        }
                    }
                }
            }

            for (Entity entity : entityList) {
                int[] entityRenderingObject = entityRenderingCache.get(entity);
                glBindBuffer(GL_ARRAY_BUFFER, entityRenderingObject[1]);
                glBufferSubData(GL_ARRAY_BUFFER, 0, getEntityVertices(entityRenderingBuffer, entity).array());
                render(entityRenderingObject[0], 4, GL_LINE_LOOP);
            }

        } catch (ExecutionException e) {
            LOGGER.error(e.getMessage());
        }

        if (zoomInKey && zoomMultiplier >= -50) {
            zoomMultiplier -= 0.1f;
            glUniform1f(2, zoomMultiplier);
        }

        if (zoomOutKey && zoomMultiplier <= 50) {
            zoomMultiplier += 0.1f;
            glUniform1f(2, zoomMultiplier);
        }

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    private FloatBuffer getEntityVertices(FloatBuffer buffer, Entity entity) {
        buffer.clear();
        Box box = entity.box;
        float x = entity.getPosition().x;
        float y = entity.getPosition().y;
        float bx = box.size.x / 2;
        float by = box.size.y / 2;
        return buffer
                .put(x - bx).put(y + by).put(getColorVertex(new Color(248, 5, 5)))
                .put(x + bx).put(y + by).put(getColorVertex(new Color(21, 13, 250)))
                .put(x + bx).put(y - by).put(getColorVertex(new Color(61, 241, 11)))
                .put(x - bx).put(y - by).put(getColorVertex(new Color(227, 227, 27)));
    }

    private FloatBuffer getBlockVertices(FloatBuffer buffer, Block block) {
        buffer.clear();
        Box box = block.box;
        float x = block.getPosition().x;
        float y = block.getPosition().y;
        float bx = box.size.x;
        float by = box.size.y;
        if (block.getPosition().equals(0, 20)) {
            return buffer
                    .put(x).put(y).put(getColorVertex(new Color(243, 11, 11)))
                    .put(x).put(y + by).put(getColorVertex(new Color(26, 71, 220)))
                    .put(x + bx).put(y + by).put(getColorVertex(new Color(101, 222, 81)))
                    .put(x + bx).put(y).put(getColorVertex(new Color(222, 182, 81)));
        } else {
            switch (block.type) {
                case "dirt" -> {
                    return buffer
                            .put(x).put(y).put(dirtColor)
                            .put(x).put(y + by).put(dirtColor)
                            .put(x + bx).put(y + by).put(dirtColor)
                            .put(x + bx).put(y).put(dirtColor);
                }
                case "stone" -> {
                    return buffer
                            .put(x).put(y).put(stoneColor)
                            .put(x).put(y + by).put(stoneColor)
                            .put(x + bx).put(y + by).put(stoneColor)
                            .put(x + bx).put(y).put(stoneColor);
                }
                case "grass" -> {
                    return buffer
                            .put(x).put(y).put(grassColor)
                            .put(x).put(y + by).put(grassColor)
                            .put(x + bx).put(y + by).put(grassColor)
                            .put(x + bx).put(y).put(grassColor);
                }
            }
        }
        return buffer
                .put(x).put(y).put(getColorVertex(new Color(243, 11, 11)))
                .put(x).put(y + by).put(getColorVertex(new Color(26, 71, 220)))
                .put(x + bx).put(y + by).put(getColorVertex(new Color(101, 222, 81)))
                .put(x + bx).put(y).put(getColorVertex(new Color(222, 182, 81)));
    }

    public static float[] getColorVertex(Color awtColor) {
        return awtColor.getColorComponents(new float[3]);
    }

    public static float[] getVertex(float x, float y, Color color) {
        float[] floatColor = color.getColorComponents(new float[3]);
        float[] vertex = new float[6];
        vertex[0] = x;
        vertex[1] = y;
        vertex[2] = 0;
        vertex[3] = floatColor[0];
        vertex[4] = floatColor[1];
        vertex[5] = floatColor[2];
        return vertex;
    }

    public static float[] getVertices(float[]... v) {
        int length = 0;
        for (float[] vertex : v) {
            length += vertex.length;
        }
        float[] vertices = new float[length];
        int i = 0;
        for (float[] vertex : v) {
            System.arraycopy(vertex, 0, vertices, i, vertex.length);
            i += vertex.length;
        }
        return vertices;
    }

    public static int loadShader(String context, int type) {
        int shader = glCreateShader(type);
        glShaderSource(shader, context);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            return 0;
        }
        return shader;
    }

    public static void render(int vao, int indices, int type) {
        glBindVertexArray(vao);
        glDrawElements(type, indices, GL_UNSIGNED_INT, 0);
    }
}
