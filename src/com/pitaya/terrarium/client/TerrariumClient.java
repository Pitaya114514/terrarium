package com.pitaya.terrarium.client;

import com.pitaya.terrarium.client.network.ClientCommunicator;
import com.pitaya.terrarium.client.network.Server;
import com.pitaya.terrarium.client.render.Renderer;
import com.pitaya.terrarium.client.window.MainWindow;
import com.pitaya.terrarium.game.Terrarium;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class TerrariumClient {
    private static final Logger LOGGER = LogManager.getLogger(TerrariumClient.class);
    public static final List<String> TITLE_LIST = List.of("Terrarium: Dig Peon, Dig!",
    "Terrarium: Epic Dirt",
    "Terrarium: Adaman-TIGHT!",
    "Terrarium: Sand is Overpowered",
    "Terrarium Part 3: The Return of the Guide",
    "Terrarium: A Bunnies Tale",
    "Terrarium: Dr. Bones and The Temple of Blood Moon",
    "Terrarium: Slimeassic Park",
    "Terrarium: The Grass is Greener on This Side",
    "Terrarium: Small Blocks, Not for Children Under the Age of 5",
    "Terrarium: There is No Cow Layer",
    "Terrarium: Suspicous Looking Eyeballs",
    "Terrarium: Purple Grass!",
    "Terrarium: No one Dug Behind!",
    "Terrarium: The Water Fall Of Content!",
    "Terrarium: Earthbound",
    "Terrarium: Dig Dug Ain't Got Nuthin on Me",
    "Terrarium: Ore's Well That Ends Well",
    "Terrarium: Judgement Clay",
    "Terrarium: Terrestrial Trouble",
    "Terrarium: Obsessive-Compulsive Discovery Simulator",
    "Terrarium: Red Dev Redemption",
    "Terrarium: Rise of the Slimes",
    "Terrarium: Now with more things to kill you!",
    "Terrarium: Rumors of the Guides' death were greatly exaggerated",
    "Terrarium: I Pity the Tools...",
    "Terrarium: A spelunker says 'What'?",
    "Terrarium: So then I said 'Something about a PC update....'",
    "Terrarium: May the blocks be with you",
    "Terrarium: Better than life",
    "Terrarium: Terrarium: Terrarium:",
    "Terrarium: Now in 1D",
    "Terrarium: Coming soon to a computer near you",
    "Terrarium: Dividing by zero",
    "Terrarium: Now with SOUND",
    "Terrarium: Press alt-f4",
    "Terrarium: I Pity the Tools",
    "Terrarium: You sand bro?",
    "Terrarium: A good day to dig hard",
    "Terrarium: Can You Re-Dig-It?",
    "Terrarium: I don't know that-- aaaaa!",
    "Terrarium: What's that purple spiked thing?",
    "Terrarium: I wanna be the guide",
    "Terrarium: Cthulhu is mad... and is missing an eye!",
    "Terrarium: NOT THE BEES!!!",
    "Terrarium: Legend of Maxx",
    "Terrarium: Cult of Cenx",
    "Terrarium 2: Electric Boogaloo",
    "Terrarium: I just wanna know where the gold at?",
    "Terrarium: Now with more ducks!",
    "Terrarium: 1 + 1 = 10",
    "Terrarium: Infinite Plantera",
    "Terrarium: Now with microtransactions!",
    "Terrarium: Built on Blockchain Technology",
    "Terrarium: Now with even less Ocram!",
    "Terrarium: Otherworld",
    "Terrarium: Touch Grass Simulator",
    "Terrarium: Don't dig up!",
    "Terrarium: For the worthy!",
    "Terrarium: Now with even more Ocram!",
    "Terrarium: Shut Up and Dig Gaiden!");

    public final Properties properties;
    public final Terrarium terrarium;
    public final GameLoader gameLoader;
    public final Renderer gameRenderer;
    public Player player;
    public final MainWindow mainWindow;
    public final ClientCommunicator communicator;
    public final Set<Server> servers;

    public TerrariumClient() {
        this.properties = new Properties();
        properties.setProperty("game-width", "1600");
        properties.setProperty("game-height", "900");
        properties.setProperty("smooth-camara", "false");
        properties.setProperty("debug-mode", "true");
        properties.setProperty("auto-aim", "true");

        String name = "client.properties";
        try (InputStream input = new FileInputStream(name)) {
            properties.load(input);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                LOGGER.warn("No properties! Properties will be generated");
                outputProperties(name);
            } else {
                LOGGER.error("e: ", e);
            }
        }

        this.servers = new HashSet<>();
        this.terrarium = new Terrarium();
        this.gameLoader = new GameLoader("saves");
        this.gameRenderer = new Renderer();
        this.mainWindow = new MainWindow();
        this.communicator = new ClientCommunicator();
        mainWindow.setVisible(true);
    }

    public void outputProperties(String name) {
        try (OutputStream output = new FileOutputStream(name)) {
            properties.store(output, "Terrarium Client Configs");
        } catch (IOException ex) {
            LOGGER.error("ex: ", ex);
        }
        try (InputStream input = new FileInputStream(name)) {
            properties.load(input);
        } catch (IOException ex) {
            LOGGER.error("ex: ", ex);

        }
    }

    public void runTerrarium(Player player) {
        this.player = player;
        terrarium.startWorld();
        terrarium.addEntity(this.player.entity());
    }

    public void terminateTerrarium() {
        terrarium.endWorld();
    }

    public void loadRenderer() {
        gameRenderer.load();
    }

    public void connectToServer(Server server) {
        communicator.connect(server);
    }
}
