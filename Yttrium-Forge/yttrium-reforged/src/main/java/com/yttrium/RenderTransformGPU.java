package com.yttrium;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

import java.nio.FloatBuffer;

public class RenderTransformGPU {

    private static int program;
    private static final Logger LOGGER = YttriumMod.LOGGER;

    // Compile the shader program
    public static void init() {
        try {
            ResourceManager rm = Minecraft.getInstance().getResourceManager();
            String source = ShaderLoader.load(rm, "RenderTransform.glsl");

            int shader = GL43.glCreateShader(GL43.GL_COMPUTE_SHADER);
            GL43.glShaderSource(shader, source);
            GL43.glCompileShader(shader);

            program = GL43.glCreateProgram();
            GL43.glAttachShader(program, shader);
            GL43.glLinkProgram(program);

            LOGGER.info("RenderTransformGPU shader compiled and linked.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize RenderTransformGPU", e);
        }
    }

    // Run the compute shader during rendering
    public static void run() {
        GL43.glUseProgram(program);

        // Example: allocate a buffer for transform data
        FloatBuffer transforms = MemoryUtil.memAllocFloat(16);
        for (int i = 0; i < 16; i++) transforms.put(i, 1.0f);

        int bufT = GL43.glGenBuffers();
        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, bufT);
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, transforms, GL43.GL_STATIC_DRAW);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, bufT);

        long start = System.nanoTime();
        GL43.glDispatchCompute(1, 1, 1);
        GL43.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
        long end = System.nanoTime();

        LOGGER.info("RenderTransformGPU complete. Time: {} ms", (end - start) / 1_000_000.0);

        MemoryUtil.memFree(transforms);
    }
}
