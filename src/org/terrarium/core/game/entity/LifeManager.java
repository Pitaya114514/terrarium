package org.terrarium.core.game.entity;

public class LifeManager {
    private double maxHealth;
    private double health;
    private double defense;

    protected LifeManager(double maxHealth, double defense) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.defense = defense;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        if (maxHealth > health && maxHealth > 0) this.maxHealth = maxHealth;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        if (health > 0 && health < maxHealth) this.health = health;
    }

    public double getDefense() {
        return defense;
    }

    public void setDefense(double defense) {
        this.defense = defense;
    }
}
