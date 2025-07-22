package org.terrarium.core.client.resources;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.openal.AL11.*;

public class SoundPack {
    public final SoundSource roar_1;
    public final SoundSource eoc_roar;
    public final SoundSource hit_1;
    public final SoundSource killed_1;

    public SoundPack(String packPath) throws ResourceLoadingException {
        if (!Files.isDirectory(Paths.get(packPath))) {
            throw new ResourceLoadingException("Directory not found: " + packPath);
        }
        roar_1 = new SoundSource(packPath + "/roar_1.ogg");
        eoc_roar = new SoundSource(packPath + "/eoc_roar.ogg");
        hit_1 = new SoundSource(packPath + "/hit_1.ogg");
        killed_1 = new SoundSource(packPath + "/killed_1.ogg");
    }

    public void free() {
        free(roar_1);
        free(eoc_roar);
        free(hit_1);
        free(killed_1);
    }

    private void free(SoundSource soundSource) {
        alDeleteSources(soundSource.source);
        alDeleteBuffers(soundSource.buffer);
    }
}
