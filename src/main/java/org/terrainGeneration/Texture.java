package org.terrainGeneration;

import org.lwjgl.stb.STBImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

public class Texture {
    public int ID;
    int type;

    public Texture(String image, int texType, int slot, int pixelType) {
        int format = GL_RGBA;
        type = texType;
        int widthImg, heightImg, numColCh;
        ByteBuffer bytes;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            bytes = STBImage.stbi_load(image, w, h, comp, 0);
            widthImg = w.get();
            heightImg = h.get();
            numColCh = comp.get();
        }
        if (numColCh == 4) {
            format = GL_RGBA;
        } else if (numColCh == 3) {
            format = GL_RGB;
        } else if (numColCh == 1) {
            format = GL_RED;
        }

        ID = glGenTextures();
        glActiveTexture(slot);
        glBindTexture(texType, ID);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(texType, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(texType, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameteri(texType, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(texType, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexImage2D(texType, 0, format, widthImg, heightImg, 0, format, pixelType, bytes);
        glGenerateMipmap(texType);

        STBImage.stbi_image_free(bytes);

        glBindTexture(texType, 0);
    }

    void texUnit(Shader shader, String uniform, int unit) {
        int texUni = glGetUniformLocation(shader.ID, uniform);
        shader.Activate();
        glUniform1i(texUni, unit);
    }

    void Bind()
    {
        glBindTexture(type, ID);
    }

    void Unbind()
    {
        glBindTexture(type, 0);
    }

    void Delete()
    {
        glDeleteTextures(ID);
    }
}
