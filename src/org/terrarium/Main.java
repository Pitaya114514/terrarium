package org.terrarium;

import org.joml.Vector2f;
import org.terrarium.base.BaseClientInitializer;
import org.terrarium.base.BaseGameInitializer;
import org.terrarium.base.BaseServerInitializer;
import org.terrarium.core.client.TerrariumClient;
import org.terrarium.core.server.TerrariumServer;

import java.io.IOException;

public final class Main {
    public static final String VERSION = "0.5.2";
    private static TerrariumClient client;
    private static TerrariumServer server;

    public static void main(String[] args) throws IOException {
        if (args.length > 0 && ("-server".equals(args[0]) || "-s".equals(args[0]) || "-S".equals(args[0]) || "-Server".equals(args[0]))) {
            Main.server = new TerrariumServer(new BaseServerInitializer(), new BaseGameInitializer());
            Thread serverThread = new Thread(server::launch);
            serverThread.setName("server");
            serverThread.start();
        } else {
            String name = "Pitaya";
            Vector2f spawnPosition = new Vector2f(0, 90);
            long seed = (long) (Math.random() * 114514);

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-name" -> {
                        if (i + 1 < args.length) name = args[i + 1];
                    }
                    case "-spawn" -> {
                        if (i + 2 < args.length) spawnPosition = new Vector2f(Float.parseFloat(args[i + 1]), Float.parseFloat(args[i + 2]));
                    }
                    case "-seed" -> {
                        if (i + 1 < args.length) seed = Long.parseLong(args[i + 1]);
                    }
                }
            }

            Main.client = new TerrariumClient(new BaseClientInitializer(), new BaseGameInitializer());
            String fName = name;
            Vector2f fSpawnPosition = spawnPosition;
            long fSeed = seed;
            Thread clientThread = new Thread(() -> client.launch(fName, fSpawnPosition, fSeed));
            clientThread.setName("client");
            clientThread.start();
        }
    }

    public static TerrariumClient getClient() {
        return client;
    }

    public static TerrariumServer getServer() {
        return server;
    }
}
