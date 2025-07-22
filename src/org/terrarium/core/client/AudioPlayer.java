package org.terrarium.core.client;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.terrarium.core.client.resources.SoundSource;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.ALC10.*;

public final class AudioPlayer {

    public void load(long window) {
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        long device;
        if (defaultDeviceName != null) {
            device = alcOpenDevice(defaultDeviceName);
        } else {
            throw new RuntimeException();
        }
        long context = alcCreateContext(device, new int[]{0});
        alcMakeContextCurrent(context);

        AL.createCapabilities(ALC.createCapabilities(device));
    }

    void tick(long window) {

    }

    private void playSound(SoundSource soundSource) {
        alSourcePlay(soundSource.source);
    }
}
