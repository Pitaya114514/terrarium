package com.pitaya.terrarium.client.render.resources;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourcePack {
    public final SoundPack soundPack;

    public ResourcePack(String packPath) throws ResourceLoadingException {
        if (!Files.isDirectory(Paths.get(packPath))) {
            throw new ResourceLoadingException();
        }
        soundPack = new SoundPack(packPath + "/sounds");
    }
}
