package com.pitaya.terrarium;

import com.pitaya.terrarium.client.TerrariumClient;
import com.pitaya.terrarium.server.TerrariumServer;

public final class Main {
    private static TerrariumClient client;
    private static TerrariumServer server;

    public static void main(String[] args) {
        assert false;
        if (args.length != 0 && ("-server".equals(args[0]) || "-s".equals(args[0]) || "-S".equals(args[0]) || "-Server".equals(args[0]))) {
            server = new TerrariumServer();
        } else {
            client = new TerrariumClient();
        }
    }

    public static TerrariumClient getClient() {
        return client;
    }

    public static TerrariumServer getServer() {
        return server;
    }
}
