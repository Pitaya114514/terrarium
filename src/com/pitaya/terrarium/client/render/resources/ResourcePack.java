package com.pitaya.terrarium.client.render.resources;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourcePack {
    public final ShaderPack shaderPack;
    public final SoundPack soundPack;
    public final TextPack textPack;
    public final TexturePack texturePack;

    public ResourcePack(String packPath) throws ResourceLoadingException {
        if (!Files.isDirectory(Paths.get(packPath))) {
            throw new ResourceLoadingException("Directory not found: " + packPath);
        }
        shaderPack = new ShaderPack(packPath + "/shaders");
        soundPack = new SoundPack(packPath + "/sounds");
        textPack = new TextPack(packPath + "/texts");
        texturePack = new TexturePack(packPath + "/textures");
    }

    public void free() {
        soundPack.free();
        texturePack.free();
    }
}
