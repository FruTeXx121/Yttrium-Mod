package com.yttrium;

import org.lwjgl.opengl.GL;

public class MatrixGPU {
    public static boolean isSupported() {
        return GL.getCapabilities().OpenGL43;
    }

    public static void runAll() {
        EntityTransformGPU.run();
        PhysicsGPU.run();
        LightingGPU.run();
        RenderTransformGPU.run();
    }

    public static void runFallback() {
        MatrixCPU.run();
    }
}
