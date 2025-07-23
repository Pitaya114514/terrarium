package org.terrarium.core.game;

import org.joml.Vector2f;
import org.terrarium.core.game.block.Block;
import org.terrarium.core.game.command.Command;
import org.terrarium.core.game.effect.Effect;
import org.terrarium.core.game.entity.Entity;
import org.terrarium.core.game.item.Item;
import org.terrarium.core.game.world.Chunk;

import java.util.List;
import java.util.Map;

public interface Terrarium {

    int executeCommand(String context);

    void addEntity(Entity entity, Vector2f pos, String name);

    void useItem(Item item);

    void sendMassage(Object sender, String message);

    int getTps();

    String getWorldName();

    Command[] getRegisteredCommands();

    Block[] getRegisteredBlocks();

    Entity[] getRegisteredEntities();

    Effect[] getRegisteredEffects();

    Item[] getRegisteredItems();

    List<Entity> getEntityList();

    Map<Entity, Chunk[]> getChunkMap();

    Block getBlock(int x, int y);

    void setBlock(int x, int y, Block block);
}
