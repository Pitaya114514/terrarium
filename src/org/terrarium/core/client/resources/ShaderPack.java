package org.terrarium.core.client.resources;

import org.terrarium.core.game.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ShaderPack {
    public final String fragment;
    public final String fragment_texture;
    public final String vertex;
    public final String vertex_texture;
    public final String entity_vertex;
    public final String entity_fragment;

    public ShaderPack(String packPath) throws ResourceLoadingException {
        if (!Files.isDirectory(Paths.get(packPath))) {
            throw new ResourceLoadingException("Directory not found: " + packPath);
        }
        try {
            fragment = Util.IO.read(packPath + "/fragment.glsl");
            fragment_texture = Util.IO.read(packPath + "/fragment_texture.glsl");
            vertex = Util.IO.read(packPath + "/vertex.glsl");
            vertex_texture = Util.IO.read(packPath + "/vertex_texture.glsl");
            entity_vertex = Util.IO.read(packPath + "/entity_vertex.glsl");
            entity_fragment = Util.IO.read(packPath + "/entity_fragment.glsl");
        } catch (IOException ex) {
            throw new ResourceLoadingException("Unable to load shader file: " + packPath, ex);
        }
    }
}
