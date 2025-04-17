package com.pitaya.terrarium.game.entity.life;

import com.pitaya.terrarium.game.entity.Box;
import com.pitaya.terrarium.game.entity.Entity;
import com.pitaya.terrarium.game.entity.MoveController;
import com.pitaya.terrarium.game.entity.barrage.BarrageEntity;

public abstract class LivingEntity extends Entity {
    public final double defaultHealth;
    public final HealthManager healthManager;
    private double health;
    private double defense;

    public LivingEntity(String name, Box box, MoveController moveController, float x, float y, double defaultHealth, double defense, int invincibilityFrame) {
        super(name, box, moveController, x, y);
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
        if (source == this || value <= 0) {
            return;
        }
        double h = value - this.defense;
        if (h < 1) {
            h = 1;
        }
        if (this.healthManager.invincibilityCD <= 0) {
            this.setHealth(this.health - h);
            if (source instanceof BarrageEntity) {
                ((BarrageEntity) source).setDurability(((BarrageEntity) source).getDefaultDurability() - 1);
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
}
