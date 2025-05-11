package com.pitaya.terrarium.game.entity.life;

import com.pitaya.terrarium.game.entity.Entity;

public final class HealthManager {
    private Entity attacker;
    public int invincibilityCD;
    public int invincibilityFrame;
    public boolean isInvincible;
    public boolean isWounded;

    public void setAttacker(Entity attacker) {
        if (attacker != null) {
            this.attacker = attacker;
        }
    }

    public Entity getAttacker() {
        return attacker;
    }
}
