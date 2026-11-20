package org.terrainGeneration;

import static org.lwjgl.opengl.GL20.*;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Shader {
    public int ID;

    String readFile(String file) throws IOException {
        Scanner sc = new Scanner(new File(file));
        String toReturn = sc.useDelimiter("\\A").next();
        sc.close();
        return toReturn;
    }

    public Shader(String vertexFile, String fragmentFile) throws IOException {
        String vertexCode = readFile(vertexFile);
        String fragCode = readFile(fragmentFile);

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexCode);
        glCompileShader(vertexShader);

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragCode);
        glCompileShader(fragmentShader);

        ID = glCreateProgram();

        glAttachShader(ID, vertexShader);
        glAttachShader(ID, fragmentShader);

        glLinkProgram(ID);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void Activate() {
        glUseProgram(ID);
    }

    public void Delete() {
        glDeleteProgram(ID);
    }
}
