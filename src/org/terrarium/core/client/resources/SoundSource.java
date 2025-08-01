package org.terrarium.core.client.resources;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;

public class SoundSource {
    public final int source;
    public final int buffer;

    protected SoundSource(String path) throws ResourceLoadingException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer channels = stack.mallocInt(1);
            IntBuffer sampleRate = stack.mallocInt(1);
            ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(path, channels, sampleRate);
            if (rawAudioBuffer == null) {
                throw new ResourceLoadingException();
            }
            int format;
            if (channels.get(0) == 1) {
                format = AL_FORMAT_MONO16;
            } else if (channels.get(0) == 2){
                format = AL_FORMAT_STEREO16;
            } else {
                format = -1;
            }
            int buffer = alGenBuffers();
            alBufferData(buffer, format, rawAudioBuffer, sampleRate.get(0));
            MemoryUtil.memFree(rawAudioBuffer);
            int source = alGenSources();
            alSourcei(source, AL_BUFFER, buffer);
            this.buffer = buffer;
            this.source = source;
        }
    }
}
