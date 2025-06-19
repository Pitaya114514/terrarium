package com.pitaya.terrarium.game.util;

import org.joml.Vector2f;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public final class Util {
    private Util() {}

    public final static class Math {
        public static final double HALF_OF_PI = java.lang.Math.PI / 2;
        private Math() {}

        public static Vector2f getRandomPos(Vector2f yourPos, float range) {
            float x = ThreadLocalRandom.current().nextFloat() * 2 * range + yourPos.x() - range;
            float y = ThreadLocalRandom.current().nextFloat() * 2 * range + yourPos.y() - range;
            return new Vector2f(x, y);
        }

        public static void movePos(Vector2f yourPos, Velocity velocity) {
            float slope = (float) java.lang.Math.tan(velocity.radians);
            float x = (float) (velocity.speed / java.lang.Math.sqrt(slope * slope + 1));
            float y = slope * x;
            if (velocity.radians >= -HALF_OF_PI && velocity.radians <= HALF_OF_PI) {
                yourPos.x += x;
                yourPos.y += y;
            } else {
                yourPos.x -= x;
                yourPos.y -= y;
            }

        }

        public static double getRadians(Vector2f yourPos, Vector2f targetPos) {
            return java.lang.Math.atan2(targetPos.y - yourPos.y, targetPos.x - yourPos.x);
        }
    }

    public final static class IO {
        private IO() {}

        public static String read(String filepath) throws IOException {
            try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
                StringBuilder context = new StringBuilder();
                while (true) {
                    String str = reader.readLine();
                    if (str == null) break;
                    context.append(str).append("\n");
                }
                return context.toString();
            }
        }

        public static String format(Iterable<?> iterable) {
            StringBuilder context = new StringBuilder();
            int n = 0;
            for (Object o : iterable) {
                context.append("\n").append(o);
                n++;
            }
            context.append("\n").append(String.format("There are %s objects in total", n));
            return context.toString();
        }

        public static String format(Map<?, ?> map) {
            StringBuilder context = new StringBuilder();
            int n = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                context.append("\n").append(entry.getKey()).append(" - ").append(entry.getValue());
                n++;
            }
            context.append("\n");
            if (n == 1) {
                context.append(String.format("There is %s object in total", n));
            } else if (n == 0) {
                context.append("There are no objects in total");
            } else {
                context.append(String.format("There are %s objects in total", n));
            }
            return context.toString();
        }
    }

    public final static class Rendering {
        private Rendering() {}

        public static float[] getColorVertex(Color awtColor) {
            return awtColor.getColorComponents(new float[3]);
        }

        public static float[] getVertex(Vector2f pos, Color color) {
            return getVertex(pos.x, pos.y, color);
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

    public static final class Counter {
        private int count;
        private final Timer timer;
        private int value;

        public Counter() {
            timer = new Timer();
        }

        public void schedule() {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    value = count;
                    count = 0;
                }
            }, 0, 1000);
        }

        public void cancel() {
            timer.cancel();
        }

        public void addCount() {
            count++;
        }

        public int getValue() {
            return value;
        }
    }
}
