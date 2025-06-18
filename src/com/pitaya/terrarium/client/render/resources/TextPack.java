package com.pitaya.terrarium.client.render.resources;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TextPack {
    public TextPack(String packPath) throws ResourceLoadingException {
        if (!Files.isDirectory(Paths.get(packPath))) {
            throw new ResourceLoadingException("Directory not found: " + packPath);
        }
    }
}
