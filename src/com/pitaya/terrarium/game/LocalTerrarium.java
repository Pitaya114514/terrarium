package com.pitaya.terrarium.game;

import com.pitaya.terrarium.game.command.Command;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.util.Util;
import com.pitaya.terrarium.game.world.IDLoader;
import com.pitaya.terrarium.game.world.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;

import java.util.Set;

public class LocalTerrarium implements Terrarium {
    public static final Logger LOGGER = LogManager.getLogger(LocalTerrarium.class);
    private final Set<Command> commandSet;
    public final IDLoader idLoader;
    private World mainWorld;

    public LocalTerrarium() {
        this.commandSet = Set.of(new Command("help") {
            @Override
            public int execute(String[] args) {
                LOGGER.info(commandSet);
                return 0;
            }
        }, new Command("summon") {
            @Override
            public int execute(String[] args) {
                try {
                    if (args.length >= 3) {
                        short id = Short.parseShort(args[0]);
                        float x = Float.parseFloat(args[1]);
                        float y = Float.parseFloat(args[2]);
                        Entity summoned = (Entity) idLoader.findEntity(id).getMethod("summon", Vector2f.class).invoke(null, new Vector2f(x, y));
                        mainWorld.addEntity(summoned);
                        return 0;
                    } else {
                        return -1;
                    }
                } catch (Exception e) {
                    LOGGER.warn("The entity cannot be summoned: ", e);
                    return 1;
                }
            }
        }, new Command("say") {
            @Override
            public int execute(String[] args) {
                if (args.length >= 1) {
                    mainWorld.chatroom.sendMessage("[Local]", String.join(" ", args));
                    return 0;
                } else {
                    return -1;
                }
            }
        }, new Command("stop") {
            @Override
            public int execute(String[] args) {
                terminateWorld();
                return 0;
            }
        }, new Command("give") {
            @Override
            public int execute(String[] args) {
                if (args.length >= 2) {
                    return 0;
                } else {
                    return -1;
                }
            }
        }, new Command("print", new Command("entities") {
            @Override
            public int execute(String[] args) {
                LOGGER.info(Util.IO.format(mainWorld.entityList));
                return 0;
            }
        }, new Command("entityid") {
            @Override
            public int execute(String[] args) {
                LOGGER.info(Util.IO.format(IDLoader.getLoader().getEntityIDMap()));
                return 0;
            }
        }, new Command("itemid") {
            @Override
            public int execute(String[] args) {
                LOGGER.info(Util.IO.format(IDLoader.getLoader().getItemIDMap()));
                return 0;
            }
        }, new Command("effectid") {
            @Override
            public int execute(String[] args) {
                LOGGER.info(Util.IO.format(IDLoader.getLoader().getEffectIDMap()));
                return 0;
            }
        }, new Command("difficulty") {
            @Override
            public int execute(String[] args) {
                LOGGER.info(mainWorld.difficulty);
                return 0;
            }
        }) {
            @Override
            public int execute(String[] args) {
                return Command.execute(String.join(" ", args), this.getCommandSet());
            }
        });
        idLoader = IDLoader.load();
    }

    @Override
    public int executeCommand(String context) {
        return Command.execute(context, commandSet);
    }

    public void startWorld() {
        if (mainWorld == null) {
            mainWorld = new World("MainWorld", 10, null);
        }
        Thread worldThread = new Thread(mainWorld);
        worldThread.setName(String.format("World-%s", mainWorld.hashCode()));
        worldThread.start();
    }

    public void terminateWorld() {
        mainWorld.stop();
    }

    @Override
    public void addEntity(Entity entity) {
        mainWorld.addEntity(entity);
    }

    @Override
    public void useItem(Item item) {
        mainWorld.addDisposableTickEventListener(event -> {
            if (item != null) {
                item.use(mainWorld);
            }
        });
    }

    public void killEntity(Entity entity) {
        mainWorld.killEntity(entity);
    }

    @Override
    public void sendMassage(Object sender, String message) {
        mainWorld.chatroom.sendMessage(sender, message);
    }

    @Override
    public int getTps() {
        return mainWorld.getTps();
    }

    @Override
    public WorldInfo getWorldInfo() {
        return mainWorld.getInfo();
    }

    public void importWorldInfo(WorldInfo worldInfo)  {
        mainWorld = new World(worldInfo.getName(), worldInfo.getGravity(), worldInfo.getDifficulty());
        if (worldInfo.getEntityList() != null) {
            for (Entity entity : worldInfo.getEntityList()) {
                mainWorld.addEntity(entity);
            }
        }
    }

    public String getWorldName() {
        return mainWorld.name;
    }
}
