package org.terrarium.core.client.resources;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class TextureSource {
    public final int source;
    public final int width;
    public final int height;
    private final ByteBuffer imageBuffer;

    protected TextureSource(String path) throws ResourceLoadingException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer imageBuffer = STBImage.stbi_load(path, width, height, channels, 4);
            if (imageBuffer == null) {
                throw new ResourceLoadingException("Failed to load image: " + STBImage.stbi_failure_reason());
            }
            this.width = width.get(0);
            this.height = height.get(0);
            this.source = glGenTextures();
            this.imageBuffer = imageBuffer;
        }
    }

    public void upload() {
        glBindTexture(GL_TEXTURE_2D, source);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public void free() {
        STBImage.stbi_image_free(this.imageBuffer);
        glDeleteTextures(this.source);
    }
}
