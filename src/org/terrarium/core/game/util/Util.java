package org.terrarium.core.game.util;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.terrarium.core.game.block.Block;
import org.terrarium.core.game.world.Chunk;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

        public static boolean isContacted(Vector2f aMin, Vector2f aMax, Vector2f bMin, Vector2f bMax) {
            boolean xOverlap = aMax.x >= bMin.x && bMax.x >= aMin.x;
            boolean yOverlap = aMax.y >= bMin.y && bMax.y >= aMin.y;
            return xOverlap && yOverlap;
        }

        public static boolean isContained(Vector2f a, Vector2f bMin, Vector2f bMax) {
            boolean withinX = (a.x >= bMin.x) && (a.x <= bMax.x);
            boolean withinY = (a.y >= bMin.y) && (a.y <= bMax.y);
            return withinX && withinY;
        }
    }

    public final static class IO {
        private static final int CHUNK_CAPACITY = 16 * 16 + 2;

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

        public static String format(Buffer buffer) {
            return Arrays.deepToString(new Object[]{buffer.array()});
        }

        public static IntBuffer serialize(Chunk chunk) {
            IntBuffer map = IntBuffer.allocate(CHUNK_CAPACITY);
            map.put(chunk.position.x).put(chunk.position.y);
            for (Block[] blocks : chunk.blockSquare) {
                for (Block block : blocks) {
                    map.put(block != null ? block.getId() : -1);
                }
            }
            return map.flip();
        }

        public static Chunk deserialize(IntBuffer data, Block[] regBlocks) {
            Vector2i position = new Vector2i(data.position(0).get(), data.get());
            int blx = 0, bly = 0;
            Block[][] blockSquare = new Block[16][16];
            do {
                int id = data.get();
                if (id >= 0) {
                    Block block = new Block(regBlocks[id]);
                    blockSquare[blx][bly] = block;
                }
                bly++;
                if (bly >= 16) {
                    bly = 0;
                    blx++;
                }
            } while (data.hasRemaining());
            return new Chunk(position, blockSquare);
        }

        public static int saveChunks(IntBuffer... chunkData) throws IOException {
            Path path = Path.of("chunks.wld");
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            try (FileChannel channel = new FileOutputStream("chunks.wld").getChannel()) {
                ByteBuffer chunkd = ByteBuffer.allocate(Integer.BYTES * chunkData.length * CHUNK_CAPACITY);
                for (IntBuffer chunkDatum : chunkData) {
                    for (int i = 0; i < CHUNK_CAPACITY; i++) {
                        chunkd.putInt(chunkDatum.get());
                    }
                }
                chunkd.flip();
                return channel.write(chunkd);
            }
        }

        public static IntBuffer[] loadChunks() throws IOException {
            if (!Files.exists(Path.of("chunks.wld"))) {
                return null;
            }
            try (FileChannel channel = new FileInputStream("chunks.wld").getChannel()) {
                ArrayList<IntBuffer> chunksd = new ArrayList<>();
                while (true) {
                    ByteBuffer chunkd = ByteBuffer.allocate(Integer.BYTES * CHUNK_CAPACITY);
                    if (channel.read(chunkd) == -1) {
                        break;
                    }
                    chunksd.add(chunkd.flip().asIntBuffer());
                }
                return chunksd.toArray(new IntBuffer[0]);
            }
        }
    }

    public static final class Counter {
        private int count;
        private final Timer timer;
        private int value;
        private int time;

        public Counter() {
            timer = new Timer();
        }

        public void schedule() {
            timer.schedule((new TimerTask() {
                @Override
                public void run() {
                    value = count;
                    count = 0;
                    time++;
                }
            }), 0, 1000);
        }

        public void schedule(TimerTask operation, int delay, int period) {
            timer.schedule(operation, delay, period);
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

        public int getTime() {
            return time;
        }
    }
}
