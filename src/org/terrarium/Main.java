package org.terrarium;

import org.terrarium.base.BaseClientInitializer;
import org.terrarium.base.BaseGameInitializer;
import org.terrarium.base.BaseServerInitializer;
import org.terrarium.core.client.TerrariumClient;
import org.terrarium.core.server.TerrariumServer;

import java.io.IOException;

public final class Main {
    public static final String VERSION = "0.5";
    private static TerrariumClient client;
    private static TerrariumServer server;

    public static void main(String[] args) throws IOException {
        if (args.length != 0 && ("-server".equals(args[0]) || "-s".equals(args[0]) || "-S".equals(args[0]) || "-Server".equals(args[0]))) {
            Main.server = new TerrariumServer(new BaseServerInitializer(), new BaseGameInitializer());
            Thread serverThread = new Thread(server::launch);
            serverThread.setName("server");
            serverThread.start();
        } else {
            Main.client = new TerrariumClient(new BaseClientInitializer(), new BaseGameInitializer());
            Thread clientThread = new Thread(client::launch);
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
