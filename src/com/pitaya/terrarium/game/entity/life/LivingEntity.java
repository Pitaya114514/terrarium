package com.pitaya.terrarium.game.entity.life;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.barrage.BarrageEntity;
import com.pitaya.terrarium.game.entity.life.mob.MobEntity;
import org.joml.Vector2f;

public abstract class LivingEntity extends Entity {
    public final double defaultHealth;
    public final HealthManager healthManager;
    private double health;
    private double defense;

    public LivingEntity(String name, Box box, MoveController moveController, Vector2f position, double defaultHealth, double defense, int invincibilityFrame) {
        super(name, box, moveController, position);
        this.defaultHealth = defaultHealth;
        this.health = defaultHealth;
        this.defense = defense;
        this.healthManager = new HealthManager();
        healthManager.invincibilityFrame = invincibilityFrame;
    }

    public double getHealth() {
        return health;
    }

    public void damage(Entity source, double value) {
        if (this instanceof MobEntity && source.box.isDangerous) {
            return;
        }
        if (!(this instanceof MobEntity) && !source.box.isDangerous) {
            return;
        }
        if (source == this || value <= 0 || healthManager.isInvincible) {
            return;
        }

        double h = value - this.defense;
        if (h < 1) {
            h = 1;
        }
        if (this.healthManager.invincibilityCD <= 0) {
            this.setHealth(this.health - h);
            if (source instanceof BarrageEntity barrage) {
                barrage.penetration++;
            }
            healthManager.setAttacker(source);
            healthManager.invincibilityCD = healthManager.invincibilityFrame;
        }
        healthManager.invincibilityCD--;
    }

    public void setHealth(double health) {
        if (health >= 0) {
            this.health = health;
        } else {
            this.health = 0;
        }
    }

    public double getDefense() {
        return defense;
    }

    public void setDefense(double defense) {
        this.defense = defense;
    }
}
