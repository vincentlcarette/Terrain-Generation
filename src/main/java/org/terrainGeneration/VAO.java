package org.terrainGeneration;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;

public class VAO {
    public int ID;

    public VAO() {
        ID = glGenVertexArrays();
    }

    void LinkAttrib(VBO vbo, int layout, int numComponents, int type, int stride, int offset)
    {
        vbo.Bind();
        glVertexAttribPointer(layout, numComponents, type, false, stride, offset);
        glEnableVertexAttribArray(layout);
        vbo.Unbind();
    }

    void Bind() {
        glBindVertexArray(ID);
    }

    void Unbind() {
        glBindVertexArray(0);
    }

    void Delete() {
        glDeleteVertexArrays(ID);
    }
}
