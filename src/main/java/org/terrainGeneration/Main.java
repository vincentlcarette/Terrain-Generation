package org.terrainGeneration;

import org.lwjgl.*;
import org.joml.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.nio.*;
import java.lang.Math;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    private static long window;
    private static int windowWidth = 1200;
    private static int windowHeight = 800;
    static Map map;
    static Water water;
    static double lastTime = glfwGetTime();
    static int frames = 0;
    static double fps = 0;
    static int divisions = 7; // How many divisions in the plane there are; the "complexity"
    static float range = 3.0f; // What portion of the noise should we use
    static float height = 0.1f; // How tall should the terrain be
    static float size = 2; // How big should the terrain be
    static float waterHeight = 0.001f;
    static float waterFrequency = 6;
    static String texture = "outline.png"; // What texture should be on each plane
    static String waterTexture = "water.png";
    static boolean waterEnabled = false;
    static float deltaTime;
    static float lastDeltaTime;

    public static void main(String[] args) throws IOException {
        lastDeltaTime = (float) glfwGetTime();
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("GLFW failed to initialize");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        window = glfwCreateWindow(windowWidth, windowHeight, "Terrain Generation", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            return;
        }

        glfwMakeContextCurrent(window);

        GL.createCapabilities();
        glfwSwapInterval(0);
        Camera cam = new Camera(windowWidth, windowHeight);
        map = new Map(divisions, range, height, size, texture, cam);
        water = new Water(divisions, waterHeight, size, waterTexture,
                waterFrequency, map, cam);

        glEnable(GL_DEPTH_TEST);

        glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_R && action == GLFW_RELEASE) {
                map.Delete();
                water.Delete();
                try {
                    float waterLevel = water.waterLevel;
                    map = new Map(divisions, range, height, size, texture, cam);
                    water = new Water(divisions, waterHeight, size, waterTexture,
                            waterFrequency, map, cam);
                    water.waterLevel = waterLevel;
                } catch (IOException e) {
                }
            }
            if (key == GLFW_KEY_T && action == GLFW_RELEASE) {
                divisions = 7;
                range = 3.0f;
                height = 0.2f;
                water.waterLevel = 0.0f;
            }
            if (key == GLFW_KEY_P && action == GLFW_PRESS) {
                waterEnabled = !waterEnabled;
            }
            if (key == GLFW_KEY_UP && action == GLFW_PRESS) {
                divisions++;
                divisions = Math.min(divisions, 10);
            }
            if (key == GLFW_KEY_DOWN && action == GLFW_PRESS) {
                divisions--;
                divisions = Math.max(divisions, 0);
            }
        });

        while (!glfwWindowShouldClose(window)) {
            deltaTime = ((float) glfwGetTime() - lastDeltaTime) * 100;
            lastDeltaTime = (float) glfwGetTime();
            frames++;
            if (glfwGetTime() - lastTime >= 1) {
                fps = frames;
                frames = 0;
                lastTime++;
            }

            int planes = (int)Math.pow(4, divisions);
            glfwSetWindowTitle(window, "Terrain Generation: " +
                    planes + " planes, " +
                    planes * 4 + " vertices, " +
                    divisions + " divisions, range: " + Math.floor(range * 10 + 0.5) / 10 +
                    ", height: " + height +
                    ", fps: " + fps);
            glClearColor(0.07f, 0.13f, 0.17f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            cam.Update(window);
            if (waterEnabled) {
                water.Draw();
            }
            map.Draw();
            glfwSwapBuffers(window);
            glfwPollEvents();
            if (glfwGetKey(window, GLFW_KEY_I) == GLFW_PRESS) {
                range += 0.1f * deltaTime;
            } else if (glfwGetKey(window, GLFW_KEY_K) == GLFW_PRESS) {
                range -= 0.1f * deltaTime;
                range = Math.max(range, 0.1f);
            }
            if (glfwGetKey(window, GLFW_KEY_U) == GLFW_PRESS) {
                height += 0.01f * deltaTime;
            } else if (glfwGetKey(window, GLFW_KEY_J) == GLFW_PRESS) {
                height -= 0.01f * deltaTime;
                height = Math.max(height, 0.03f);
            }
            if (glfwGetKey(window, GLFW_KEY_O) == GLFW_PRESS) {
                water.waterLevel += 0.001f * deltaTime;
            } else if (glfwGetKey(window, GLFW_KEY_L) == GLFW_PRESS) {
                water.waterLevel -= 0.001f * deltaTime;
            }
            if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
                break;
            }
        }

        map.Delete();
        water.Delete();
        glfwDestroyWindow(window);
        glfwTerminate();
    }
}
