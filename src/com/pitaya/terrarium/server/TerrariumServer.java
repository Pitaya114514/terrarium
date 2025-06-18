package com.pitaya.terrarium.server;

import com.pitaya.terrarium.game.LocalTerrarium;
import com.pitaya.terrarium.game.Terrarium;
import com.pitaya.terrarium.server.network.ServerCommunicator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class TerrariumServer {
    private static final Logger LOGGER = LogManager.getLogger(TerrariumServer.class);
    public static final String SERVER_VERSION = "b1.3.5-server";
    public final Properties properties;
    private Terrarium terrarium;
    public final ServerCommunicator communicator;

    public TerrariumServer() {
        LOGGER.info("Terrarium | {}", SERVER_VERSION);
        this.properties = new Properties();
        properties.setProperty("server-port", "25565");
        properties.setProperty("server-ip", "127.0.0.1");
        properties.setProperty("console-mode", "false");

        String name = "server.properties";
        try (InputStream input = new FileInputStream(name)) {
            properties.load(input);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                LOGGER.warn("No properties! Properties will be generated");
                outputProperties(name);
            } else {
                LOGGER.error("Properties loading failed:", e);
            }
        }

        try {
            communicator = new ServerCommunicator(Integer.parseInt(properties.getProperty("server-port")), InetAddress.getByName(properties.getProperty("server-ip")));
        } catch (UnknownHostException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
        runTerrarium();
    }

    public void runTerrarium() {
        this.terrarium = new LocalTerrarium();
        LocalTerrarium lTerrarium = (LocalTerrarium) this.terrarium;
        lTerrarium.startWorld();
        communicator.load();
        Thread scanner;
        if (!Boolean.parseBoolean(properties.getProperty("console-mode"))) {
            scanner = new Thread(() -> {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    try {
                        String context = reader.readLine();
                        if (context.isEmpty()) {
                            continue;
                        }
                        int result = lTerrarium.executeCommand(context);
                        if (result == -1) {
                            LOGGER.warn("Command not found: {}", context);
                        }
                    } catch (IOException e) {
                        LOGGER.warn("Unable to execute command: ", e);
                    }

                }
            });
        } else {
            scanner = new Thread(() -> {
                Console console = System.console();
                while (true) {
                    String context = console.readLine();
                    if (context.isEmpty()) {
                        continue;
                    }
                    int result = lTerrarium.executeCommand(context);
                    if (result == -1) {
                        LOGGER.warn("Command not found: {}", context);
                    }
                    console.flush();
                }
            });
        }

        scanner.setName("ScannerThread");
        scanner.start();
    }

    public void outputProperties(String name) {
        try (OutputStream output = new FileOutputStream(name)) {
            properties.store(output, "Terrarium Server Configs");
        } catch (IOException ex) {
            LOGGER.error("Properties generation failed: ", ex);
        }
        try (InputStream input = new FileInputStream(name)) {
            properties.load(input);
        } catch (IOException ex) {
            LOGGER.error("Properties loading failed: ", ex);

        }
    }

    public Terrarium getTerrarium() {
        return terrarium;
    }
}
