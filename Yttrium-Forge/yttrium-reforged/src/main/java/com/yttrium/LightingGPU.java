package com.yttrium;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class LightingGPU {

    private static int program;
    private static final Logger LOGGER = YttriumMod.LOGGER;

    // Compile and link the compute shader
    public static void init() {
        try {
            ResourceManager rm = Minecraft.getInstance().getResourceManager();
            String source = ShaderLoader.load(rm, "LightingCompute.glsl"); // GLSL file in assets/yttrium/shaders/

            int shader = GL43.glCreateShader(GL43.GL_COMPUTE_SHADER);
            GL43.glShaderSource(shader, source);
            GL43.glCompileShader(shader);

            program = GL43.glCreateProgram();
            GL43.glAttachShader(program, shader);
            GL43.glLinkProgram(program);

            LOGGER.info("LightingGPU shader compiled and linked.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize LightingGPU", e);
        }
    }

    // Run the compute shader
    public static void run() {
        int size = 16 * 16;
        FloatBuffer light = MemoryUtil.memAllocFloat(size);
        IntBuffer blocks = MemoryUtil.memAllocInt(size);

        for (int i = 0; i < size; i++) {
            light.put(i, 1.0f);
            blocks.put(i, (i % 5 == 0) ? 1 : 0); // simulate some occlusion
        }

        GL43.glUseProgram(program);

        int bufL = GL43.glGenBuffers();
        int bufB = GL43.glGenBuffers();

        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, bufL);
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, light, GL43.GL_STATIC_DRAW);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, bufL);

        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, bufB);
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, blocks, GL43.GL_STATIC_DRAW);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, bufB);

        long start = System.nanoTime();
        GL43.glDispatchCompute(1, 1, 1); // 16x16 = 256 threads
        GL43.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
        long end = System.nanoTime();

        LOGGER.info("LightingGPU complete. Time: {} ms", (end - start) / 1_000_000.0);

        MemoryUtil.memFree(light);
        MemoryUtil.memFree(blocks);
    }
}
