package com.pitaya.terrarium.game.entity.life;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.barrage.Bullet;
import com.pitaya.terrarium.game.entity.barrage.Explosion;
import com.pitaya.terrarium.game.entity.barrage.Shell;
import com.pitaya.terrarium.game.item.Backpack;
import com.pitaya.terrarium.game.item.other.HeartOfOldManItem;
import com.pitaya.terrarium.game.item.other.RodOfDiscordItem;
import com.pitaya.terrarium.game.item.other.SlimeCrownItem;
import com.pitaya.terrarium.game.item.other.SuspiciousLookingEyeItem;
import com.pitaya.terrarium.game.item.weapon.MeteorStaffItem;
import com.pitaya.terrarium.game.item.weapon.ShellLauncherItem;
import org.joml.Vector2f;

public class PlayerEntity extends LivingEntity {
    private final Backpack backpack = new Backpack(this, 30);
    public final Vector2f targetPos = new Vector2f();

    public PlayerEntity(String name, float x, float y) {
        super(name, new Box(10, 10, 0), new MoveController(false), x, y, 100, 0, 50);
        backpack.addItem(new ShellLauncherItem());
        HeartOfOldManItem item = new HeartOfOldManItem();
        backpack.addItem(item);
        SlimeCrownItem item1 = new SlimeCrownItem();
        backpack.addItem(item1);
        SuspiciousLookingEyeItem item2 = new SuspiciousLookingEyeItem();
        backpack.addItem(item2);
        MeteorStaffItem item3 = new MeteorStaffItem();
        backpack.addItem(item3);
        backpack.addItem(new RodOfDiscordItem());
    }

    public Backpack getBackpack() {
        return backpack;
    }

    @Override
    public void damage(Entity source, double value) {
        if (source instanceof Shell || source instanceof Explosion || source instanceof Bullet) {
            return;
        }
        super.damage(source, value);
    }
}
