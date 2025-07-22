package org.terrarium.core.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.terrarium.Main;
import org.terrarium.core.config.ConfigManager;
import org.terrarium.core.game.GameInitializer;
import org.terrarium.core.game.LocalTerrarium;
import org.terrarium.core.game.util.Util;
import org.terrarium.core.server.network.ServerCommunicator;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TerrariumServer {
    private static final Logger LOGGER = LogManager.getLogger(TerrariumServer.class);
    public final GameInitializer gameInitializer;
    public final ConfigManager configManager;
    public final ServerCommunicator communicator;
    private LocalTerrarium terrarium;
    private Util.Counter tpsCounter;
    boolean fla = true;

    public TerrariumServer(ServerInitializer sInitializer, GameInitializer gInitializer) {
        LOGGER.info("Terrarium Server | {}", Main.VERSION);
        this.gameInitializer = gInitializer;
        this.configManager = new ConfigManager("server", "Server properties", sInitializer.init());
        this.communicator = new ServerCommunicator(25565, InetAddress.getLoopbackAddress());
    }

    public void launch() {
        runTerrarium(gameInitializer, 1919810);
        communicator.load();
        tpsCounter = new Util.Counter();
        tpsCounter.schedule();
        while (fla) {
            tick();
            tpsCounter.addCount();
        }
        tpsCounter.cancel();
        terrarium.stopWorld();
    }

    private void tick() {
        communicator.tick(terrarium);
    }

    private void runTerrarium(GameInitializer initializer, long seed) {
        this.terrarium = new LocalTerrarium(initializer);
        LOGGER.info("Loading world: {}", "MainWorld");
        terrarium.startWorld(seed);
    }

    public LocalTerrarium getTerrarium() {
        return terrarium;
    }
}
