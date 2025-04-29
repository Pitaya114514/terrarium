package com.pitaya.terrarium.client.render;

import com.pitaya.terrarium.Main;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL33;

public final class Hud {
    public enum HudItems {
        PLAYER_HEALTH_BAR(new RenderModule("PlayerHealthBar", true) {
            @Override
            public void renderFunction(Vector2f windowSize) {
                GL33.glPushMatrix();
                GL33.glColor3f(1.0f, 0.1f, 0.0f);
                GL33.glBegin(GL33.GL_QUADS);
                float health = (float) (5 + Main.getClient().player.entity().getHealth());
                GL33.glVertex2f(windowSize.x() - 130 + 5, 5);
                GL33.glVertex2f(windowSize.x() - 130 + 5, 20);
                GL33.glVertex2f(windowSize.x() - 130 + health, 20);
                GL33.glVertex2f(windowSize.x() - 130 + health, 5);
                GL33.glEnd();
                GL33.glPopMatrix();
            }
        }),

        BOSS_HEALTH_BAR(new BossHealthBar(true)),

        CHAT_BAR(new CharBar(false));

        private RenderModule renderModule;

        HudItems(RenderModule renderModule) {
            this.renderModule = renderModule;
        }

        public RenderModule getRenderModule() {
            return renderModule;
        }

        public void setRenderModule(RenderModule renderModule) {
            this.renderModule = renderModule;
        }
    }

    private boolean isEnable;

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public void render(Vector2f pos1, Vector2f pos2, Vector2f pos3, Vector2f pos4, int type, float r, float g, float b) {
        GL33.glPushMatrix();
        GL33.glColor3f(r,g,b);
        GL33.glBegin(type);
        GL33.glVertex2f(pos1.x(), pos1.y());
        GL33.glVertex2f(pos2.x(), pos2.y());
        if (type == GL33.GL_TRIANGLES) {
            GL33.glVertex2f(pos3.x(), pos3.y());
        }
        if (type == GL33.GL_QUADS) {
            GL33.glVertex2f(pos3.x(), pos3.y());
            GL33.glVertex2f(pos4.x(), pos4.y());
        }
        GL33.glEnd();
        GL33.glPopMatrix();
    }

    public void render(Renderable renderable, Vector2f windowSize) {
        renderable.render(windowSize);
    }
}
