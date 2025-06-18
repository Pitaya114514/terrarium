package com.pitaya.terrarium.client.render.resources;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

public class TexturePack {
    public final TextureSource forest_background_14;
    public final TextureSource temp_player;

    public TexturePack(String packPath) throws ResourceLoadingException {
        if (!Files.isDirectory(Paths.get(packPath))) {
            throw new ResourceLoadingException("Directory not found: " + packPath);
        }
        this.forest_background_14 = new TextureSource(packPath + "/forest_background_14.png");
        this.temp_player = new TextureSource(packPath + "/temp_player.png");
    }

    public void free() {
        forest_background_14.free();
        temp_player.free();
    }
}
