package org.terrarium.core.client.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joml.Vector2f;
import org.terrarium.core.game.Terrarium;
import org.terrarium.core.game.block.Block;
import org.terrarium.core.game.command.Command;
import org.terrarium.core.game.effect.Effect;
import org.terrarium.core.game.entity.Entity;
import org.terrarium.core.game.item.Item;
import org.terrarium.core.game.world.Chunk;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RemoteTerrarium implements Terrarium {
    private final ClientCommunicator communicator;
    private final ObjectMapper mapper = new ObjectMapper();

    private Command[] registeredCommands;
    private Block[] registeredBlocks;
    private Entity[] registeredEntities;
    private Effect[] registeredEffects;
    private Item[] registeredItems;

    public RemoteTerrarium(ClientCommunicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public int executeCommand(String context) {
        return 0;
    }

    @Override
    public void addEntity(Entity entity, Vector2f pos, String name) {

    }

    @Override
    public void useItem(Item item) {

    }


    @Override
    public void sendMassage(Object sender, String message) {

    }

    @Override
    public int getTps() {
        return 0;
    }

    @Override
    public String getWorldName() {
        return "";
    }

    @Override
    public Command[] getRegisteredCommands() {
        communicator.send(-2);
        return null;
    }

    @Override
    public Block[] getRegisteredBlocks() {
        communicator.send(-3);
        return null;
    }

    @Override
    public Entity[] getRegisteredEntities() {
        if (registeredEntities == null) {
            communicator.send(-4);
            try {
                this.registeredEntities = mapper.readValue(communicator.receive(), Entity[].class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return registeredEntities.clone();
    }

    @Override
    public Effect[] getRegisteredEffects() {
        communicator.send(-5);
        return null;
    }

    @Override
    public Item[] getRegisteredItems() {
        communicator.send(-6);
        return null;
    }

    @Override
    public List<Entity> getEntityList() {
        return List.of();
    }

    @Override
    public Map<Entity, Chunk[]> getChunkMap() {
        return Map.of();
    }

    @Override
    public Block getBlock(int x, int y) {
        return null;
    }
}
