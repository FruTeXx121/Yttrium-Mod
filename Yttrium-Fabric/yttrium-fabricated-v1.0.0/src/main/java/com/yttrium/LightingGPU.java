package com.yttrium;

import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class LightingGPU {
    public static void run() {
        int size = 16 * 16;
        FloatBuffer light = MemoryUtil.memAllocFloat(size);
        IntBuffer blocks = MemoryUtil.memAllocInt(size);

        for (int i = 0; i < size; i++) {
            light.put(i, 1.0f);
            blocks.put(i, (i % 5 == 0) ? 1 : 0); // simulate some occlusion
        }

        int shader = GL43.glCreateShader(GL43.GL_COMPUTE_SHADER);
        GL43.glShaderSource(shader, ShaderLoader.load("assets/yttrium/shaders/LightingCompute.glsl"));
        GL43.glCompileShader(shader);

        int program = GL43.glCreateProgram();
        GL43.glAttachShader(program, shader);
        GL43.glLinkProgram(program);
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

        YttriumMod.LOGGER.info("Lighting GPU complete. Time: {} ms", (end - start) / 1_000_000.0);


        MemoryUtil.memFree(light);
        MemoryUtil.memFree(blocks);
    }
}
