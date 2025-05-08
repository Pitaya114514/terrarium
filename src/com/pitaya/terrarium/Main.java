package com.pitaya.terrarium;

import com.pitaya.terrarium.client.TerrariumClient;
import com.pitaya.terrarium.server.TerrariumServer;

import java.util.Objects;

public final class Main {
    private static TerrariumClient client;
    private static TerrariumServer server;

    public static void main(String[] args) {
        if (args.length != 0 && Objects.equals(args[0], "-server")) {
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
