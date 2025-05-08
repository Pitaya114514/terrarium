package com.pitaya.terrarium.game.entity.life;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.barrage.Bullet;
import com.pitaya.terrarium.game.entity.barrage.Explosion;
import com.pitaya.terrarium.game.entity.barrage.Shell;
import com.pitaya.terrarium.game.entity.life.mob.boss.MoonLordCoreEntity;
import com.pitaya.terrarium.game.item.Backpack;
import com.pitaya.terrarium.game.item.other.*;
import com.pitaya.terrarium.game.item.weapon.MeteorStaffItem;
import com.pitaya.terrarium.game.item.weapon.ShellLauncherItem;
import org.joml.Vector2f;

public class PlayerEntity extends LivingEntity {
    private final Backpack backpack = new Backpack(this, 30);
    public final Vector2f targetPos = new Vector2f();

    public PlayerEntity(String name, float x, float y) {
        super(name, new Box(20, 30, 0, false), new MoveController(false), x, y, 100, 0, 50);
        backpack.addItem(new ShellLauncherItem());
        SlimeCrownItem item1 = new SlimeCrownItem();
        backpack.addItem(item1);
        SuspiciousLookingEyeItem item2 = new SuspiciousLookingEyeItem();
        backpack.addItem(item2);
        MeteorStaffItem item3 = new MeteorStaffItem();
        backpack.addItem(item3);
        backpack.addItem(new RodOfDiscordItem());
        backpack.addItem(new CelestialSigilItem());
    }

    public Backpack getBackpack() {
        return backpack;
    }

}
