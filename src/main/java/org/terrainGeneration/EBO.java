package org.terrainGeneration;

import static org.lwjgl.opengl.GL15.*;

public class EBO {
    public int ID;

    public EBO(int[] indices) {
        ID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public void Bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ID);
    }

    public void Unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void Delete() {
        glDeleteBuffers(ID);
    }
}
