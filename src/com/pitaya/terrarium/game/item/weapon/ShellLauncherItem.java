package com.pitaya.terrarium.game.item.weapon;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.game.entity.barrage.Shell;
import com.pitaya.terrarium.game.item.Item;
import com.pitaya.terrarium.game.world.World;

public class ShellLauncherItem extends Item {
    public ShellLauncherItem() {
        super("ShellLauncher");
    }

    @Override
    public void use(World world) {
        super.use(world);
        world.addEntity(new Shell(Main.getClient().player.entity().position, Main.getClient().player.cursorPos, 6));
        world.addEntity(new Shell(Main.getClient().player.entity().position, Main.getClient().player.cursorPos, 7));
        world.addEntity(new Shell(Main.getClient().player.entity().position, Main.getClient().player.cursorPos, 8));
        world.addEntity(new Shell(Main.getClient().player.entity().position, Main.getClient().player.cursorPos, 9));
        world.addEntity(new Shell(Main.getClient().player.entity().position, Main.getClient().player.cursorPos, 10));
    }
}
