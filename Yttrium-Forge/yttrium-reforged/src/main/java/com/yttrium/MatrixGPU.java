package com.yttrium;

import org.lwjgl.opengl.GL;
import org.slf4j.Logger;

public class MatrixGPU {

    private static final Logger LOGGER = YttriumMod.LOGGER;

    public static boolean isSupported() {
        return GL.getCapabilities().OpenGL43;
    }

    public static void runAll() {
        LOGGER.info("MatrixGPU: running all GPU shaders");
        EntityTransformGPU.run();
        PhysicsGPU.run();
        LightingGPU.run(); // updated reference
        RenderTransformGPU.run();
    }

    public static void runFallback() {
        LOGGER.warn("MatrixGPU: GPU not supported, falling back to CPU");
        MatrixCPU.run();
    }
}
