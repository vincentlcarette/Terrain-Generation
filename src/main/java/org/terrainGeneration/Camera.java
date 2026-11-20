package org.terrainGeneration;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.lang.Math;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    Vector3f position = new Vector3f(0, 0.5f, -2.0f);
    Vector3f orientation = new Vector3f(0, 0, 1.0f);
    Matrix4f camMatrix = new Matrix4f();
    int width, height;
    float speed = 2000.0f;
    double lastTime = glfwGetTime();
    boolean firstClick = true;
    float sensitivity = 50.0f;

    public Camera(int windowWidth, int windowHeight) {
        width = windowWidth;
        height = windowHeight;
    }

    public void Update(long window) {
        Matrix4f view = new Matrix4f();
        Matrix4f proj = new Matrix4f();
        Vector3f posPlusOr = new Vector3f(0, 0, 0);
        position.add(orientation, posPlusOr);

        view.lookAt(position, posPlusOr, new Vector3f(0, 1, 0));
        proj.perspective((float) Math.toRadians(45), (float) width / height, 0.01f, 100);
        proj.mul(view, camMatrix);

        float dt = (float) (glfwGetTime() - lastTime) / 1000;
        lastTime = glfwGetTime();

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            position.add(new Vector3f(orientation).mul(speed).mul(dt));
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            position.add(new Vector3f(orientation).mul(-speed).mul(dt));
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            position.add(new Vector3f(orientation).cross(new Vector3f(0, 1, 0))
                    .normalize().mul(-speed).mul(dt));
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            position.add(new Vector3f(orientation).cross(new Vector3f(0, 1, 0))
                    .normalize().mul(speed).mul(dt));
        }
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

            if (firstClick)
            {
                glfwSetCursorPos(window, (width / 2), (height / 2));
                firstClick = false;
            }

            DoubleBuffer mouseXBuff = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer mouseYBuff = BufferUtils.createDoubleBuffer(1);

            glfwGetCursorPos(window, mouseXBuff, mouseYBuff);

            float mouseX = (float) mouseXBuff.get(), mouseY = (float) mouseYBuff.get();

            float rotX = sensitivity * (mouseY - (height / 2)) / height;
            float rotY = sensitivity * (mouseX - (width / 2)) / width;

            Vector3f axis = new Vector3f(orientation).cross(new Vector3f(0, 1, 0)).normalize();
            Vector3f newOrientation = new Vector3f(orientation).rotateAxis(
                    (float) Math.toRadians(-rotX),
                    axis.x, axis.y, axis.z);

            if (Math.abs(newOrientation.angle(new Vector3f(0, 1, 0))) - Math.toRadians(90)
                    <= Math.toRadians(85)) {
                orientation = new Vector3f(newOrientation);
            }

            orientation.rotateY((float) Math.toRadians(-rotY));

            glfwSetCursorPos(window, (width / 2), (height / 2));
        } else if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_RELEASE) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            firstClick = true;
        }
    }
}
