package com.pitaya.terrarium.client.render.resources;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SoundPack {
    public final Sound roar_1;
    public final Sound eoc_roar;
    public final Sound hit_1;
    public final Sound killed_1;

    protected SoundPack(String packPath) throws ResourceLoadingException {
        if (!Files.isDirectory(Paths.get(packPath))) {
            throw new ResourceLoadingException();
        }
        roar_1 = new Sound(packPath + "/roar_1.ogg");
        eoc_roar = new Sound(packPath + "/eoc_roar.ogg");
        hit_1 = new Sound(packPath + "/hit_1.ogg");
        killed_1 = new Sound(packPath + "/killed_1.ogg");
    }
}
