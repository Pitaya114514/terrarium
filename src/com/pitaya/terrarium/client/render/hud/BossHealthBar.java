package com.pitaya.terrarium.client.render.hud;

import com.pitaya.terrarium.client.render.RenderModule;
import com.pitaya.terrarium.game.entity.life.LivingEntity;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL33;

public class BossHealthBar extends RenderModule {
    private LivingEntity targetEntity;

    public BossHealthBar(boolean isEnable) {
        super("BossHealthBar", isEnable);
    }

    public LivingEntity getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(LivingEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    @Override
    public void renderFunction(Vector2f windowSize) {
        if (targetEntity != null) {
            GL33.glPushMatrix();
            GL33.glColor3f(1.0f, 0.0f, 0.0f);
            GL33.glBegin(GL33.GL_QUADS);
            float healthPercentage = (float) ((5 + targetEntity.getHealth()) / targetEntity.defaultHealth);
            float x1 = windowSize.x() / 2 - 300 * healthPercentage;
            float x2 = windowSize.x() / 2 + 300 * healthPercentage;
            float y1 = windowSize.y() - 25;
            float y2 = windowSize.y() - 5;
            GL33.glVertex2f(x1, y1);
            GL33.glVertex2f(x1, y2);
            GL33.glVertex2f(x2, y2);
            GL33.glVertex2f(x2, y1);
            GL33.glEnd();
            GL33.glPopMatrix();
        }
    }
}
