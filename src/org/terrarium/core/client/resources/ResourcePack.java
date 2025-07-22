package org.terrarium.core.client.resources;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourcePack {
    private final String packPath;
    private ShaderPack shaderPack;
    private SoundPack soundPack;
    private TextPack textPack;
    private TexturePack texturePack;

    public ResourcePack(String packPath) throws ResourceLoadingException {
        if (!Files.isDirectory(Paths.get(packPath))) {
            throw new ResourceLoadingException("Directory not found: " + packPath);
        }
        this.packPath = packPath;
    }

    public void loadShaderPack() throws ResourceLoadingException {
        shaderPack = new ShaderPack(packPath + "/shaders");
    }

    public void loadSoundPack() throws ResourceLoadingException {
        soundPack = new SoundPack(packPath + "/sounds");
    }

    public void loadTextPack() throws ResourceLoadingException {
        textPack = new TextPack(packPath + "/texts");
    }

    public void loadTexturePack() throws ResourceLoadingException {
        texturePack = new TexturePack(packPath + "/textures");
    }

    public void free() {
        if (soundPack != null) {
            soundPack.free();
        }
        if (texturePack != null) {
            texturePack.free();
        }
    }

    public ShaderPack getShaderPack() {
        return shaderPack;
    }

    public SoundPack getSoundPack() {
        return soundPack;
    }

    public TextPack getTextPack() {
        return textPack;
    }

    public TexturePack getTexturePack() {
        return texturePack;
    }
}
