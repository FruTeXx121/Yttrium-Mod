package com.yttrium;

import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class EntityTransformGPU {
    public static void run() {
        int count = 64;
        FloatBuffer transforms = MemoryUtil.memAllocFloat(count * 16);
        FloatBuffer positions = MemoryUtil.memAllocFloat(count * 4);
        FloatBuffer results = MemoryUtil.memAllocFloat(count * 4);

        for (int i = 0; i < count * 16; i++) transforms.put(i, 1.0f);
        for (int i = 0; i < count * 4; i++) positions.put(i, 1.0f);

        int shader = GL43.glCreateShader(GL43.GL_COMPUTE_SHADER);
        GL43.glShaderSource(shader, ShaderLoader.load("assets/yttrium/shaders/EntityTransform.glsl"));
        GL43.glCompileShader(shader);

        int program = GL43.glCreateProgram();
        GL43.glAttachShader(program, shader);
        GL43.glLinkProgram(program);
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

        YttriumMod.LOGGER.info("Entity transform GPU complete. Time: {} ms", (end - start) / 1_000_000.0);


        MemoryUtil.memFree(transforms);
        MemoryUtil.memFree(positions);
        MemoryUtil.memFree(results);
    }
}
