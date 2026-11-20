package org.terrainGeneration;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.glfw.GLFW.*;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Water {
    float plane[] =
            { //     COORDINATES     /        COLORS      /   TexCoord  //
                    -1.0f, 0.0f,  1.0f,     1.0f,  1.0f,   1.0f,    0.0f, 1.0f,
                    -1.0f, 0.0f, -1.0f,     1.0f,  1.0f,   1.0f,	1.0f, 1.0f,
                    1.0f, 0.0f, -1.0f,     1.0f,  1.0f,   1.0f,	1.0f, 0.0f,
                    1.0f, 0.0f,  1.0f,     1.0f,  1.0f,   1.0f,	0.0f, 0.0f
            };

    // Indices for vertices order
    int planeIndices[] =
            {
                    0, 1, 2,
                    2, 3, 0
            };
    int indicesNum;
    Shader shader;
    Texture tex;
    VAO vao;
    VBO vbo;
    EBO ebo;
    Camera camera;
    public float waterLevel = 0.0f;

    public Water(float divisions, float height, float size, String texture,
                 float frequency, Map map, Camera cam) throws IOException {
        camera = cam;
        int numPlanes = (int)Math.pow(4, divisions);
        indicesNum = numPlanes * 6;
        float[] vertices = new float[numPlanes * 32];
        int[] indices = new int[indicesNum];

        double gridSize = Math.sqrt(numPlanes);

        for (int i = 0; i < numPlanes; i++) {
            for (int k = 0; k < plane.length; k++) {
                float num = plane[k];
                if (k % 8 <= 2) {
                    num /= gridSize;
                    if (k % 8 == 0) {
                        num += (i % gridSize) * (2.0f / gridSize);
                    }
                    if (k % 8 == 2) {
                        num += Math.floor(i / gridSize) * (2.0f / gridSize);
                    }
                    num *= size;
                }
                if (k % 8 == 4) {
                    num = map.vertices[i * plane.length + 1];
                }
                vertices[i * plane.length + k] = num;
            }
            for (int k = 0; k < planeIndices.length; k++) {
                indices[i * 6 + k] = planeIndices[k] + i * 4;
            }
        }

        shader = new Shader("./src/main/resources/waterVert.txt", "./src/main/resources/waterFrag.txt");

        vao = new VAO();
        vao.Bind();

        vbo = new VBO(vertices);
        ebo = new EBO(indices);

        vao.LinkAttrib(vbo, 0, 3, GL_FLOAT, 8 * Float.BYTES, 0);
        vao.LinkAttrib(vbo, 1, 3, GL_FLOAT, 8 * Float.BYTES, 3 * Float.BYTES);
        vao.LinkAttrib(vbo, 2, 2, GL_FLOAT, 8 * Float.BYTES, 6 * Float.BYTES);

        vao.Unbind();
        vbo.Unbind();
        ebo.Unbind();

        tex = new Texture(texture, GL_TEXTURE_2D, GL_TEXTURE0, GL_UNSIGNED_BYTE);
        tex.texUnit(shader, "tex0", 0);
        shader.Activate();

        glUniform1f(glGetUniformLocation(shader.ID, "height"), height);
        glUniform1f(glGetUniformLocation(shader.ID, "frequency"), frequency);
    }

    public void Draw() {
        shader.Activate();
        glUniform1f(glGetUniformLocation(shader.ID, "time"), (float)glfwGetTime());

        Matrix4f model = new Matrix4f();

        model.translate(0, waterLevel, 0);

        FloatBuffer modelBuffer = BufferUtils.createFloatBuffer(16);
        FloatBuffer camBuffer = BufferUtils.createFloatBuffer(16);

        model.get(modelBuffer);
        camera.camMatrix.get(camBuffer);

        glUniformMatrix4fv(glGetUniformLocation(shader.ID, "model"), false, modelBuffer);
        glUniformMatrix4fv(glGetUniformLocation(shader.ID, "cam"), false, camBuffer);

        tex.Bind();
        vao.Bind();
        glDrawElements(GL_TRIANGLES, indicesNum, GL_UNSIGNED_INT, 0);
    }

    public void Delete() {
        vao.Delete();
        vbo.Delete();
        ebo.Delete();
        shader.Delete();
    }
}
