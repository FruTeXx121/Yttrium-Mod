package com.yttrium;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

import java.nio.FloatBuffer;

public class EntityTransformGPU {

    private static int program;
    private static final Logger LOGGER = YttriumMod.LOGGER;

    // Compile and link the compute shader
    public static void init() {
        try {
            ResourceManager rm = Minecraft.getInstance().getResourceManager();
            String source = ShaderLoader.load(rm, "EntityTransform.glsl");

            int shader = GL43.glCreateShader(GL43.GL_COMPUTE_SHADER);
            GL43.glShaderSource(shader, source);
            GL43.glCompileShader(shader);

            program = GL43.glCreateProgram();
            GL43.glAttachShader(program, shader);
            GL43.glLinkProgram(program);

            LOGGER.info("EntityTransformGPU shader compiled and linked.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize EntityTransformGPU", e);
        }
    }

    // Run the compute shader
    public static void run() {
        int count = 64;
        FloatBuffer transforms = MemoryUtil.memAllocFloat(count * 16);
        FloatBuffer positions = MemoryUtil.memAllocFloat(count * 4);
        FloatBuffer results = MemoryUtil.memAllocFloat(count * 4);

        for (int i = 0; i < count * 16; i++) transforms.put(i, 1.0f);
        for (int i = 0; i < count * 4; i++) positions.put(i, 1.0f);

        GL43.glUseProgram(program);

        int bufT = GL43.glGenBuffers();
        int bufP = GL43.glGenBuffers();
        int bufR = GL43.glGenBuffers();

        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, bufT);
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, transforms, GL43.GL_STATIC_DRAW);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, bufT);

        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, bufP);
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, positions, GL43.GL_STATIC_DRAW);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, bufP);

        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, bufR);
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, results, GL43.GL_STATIC_DRAW);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 2, bufR);

        long start = System.nanoTime();
        GL43.glDispatchCompute(count / 64, 1, 1);
        GL43.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
        long end = System.nanoTime();

        LOGGER.info("EntityTransformGPU complete. Time: {} ms", (end - start) / 1_000_000.0);

        MemoryUtil.memFree(transforms);
        MemoryUtil.memFree(positions);
        MemoryUtil.memFree(results);
    }
}
