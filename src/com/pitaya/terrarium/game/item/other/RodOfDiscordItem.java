package com.pitaya.terrarium.game.item.other;

import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.world.World;

public class RodOfDiscordItem extends Item {
    public RodOfDiscordItem() {
        super("RodOfDiscord");
    }

    @Override
    public void use(World world) {
        super.use(world);
        owner.position.set(owner.targetPos);
    }
}
