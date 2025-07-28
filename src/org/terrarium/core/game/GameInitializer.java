package org.terrarium.core.game;

import org.terrarium.core.game.block.Block;
import org.terrarium.core.game.command.Command;
import org.terrarium.core.game.effect.Effect;
import org.terrarium.core.game.entity.Entity;
import org.terrarium.core.game.item.Item;
import org.terrarium.core.game.world.WorldGenerator;

public interface GameInitializer {
    Command[] initCommands();

    Block[] initBlocks();

    Entity[] initEntities();

    Effect[] initEffects();

    Item[] initItems();

    WorldGenerator[] getWorldGenerators(Block[] regBlocks);
}
