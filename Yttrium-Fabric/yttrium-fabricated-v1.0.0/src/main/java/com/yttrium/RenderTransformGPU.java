package com.yttrium;

import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class RenderTransformGPU {
    public static void run() {
        int count = 64;
        FloatBuffer verts = MemoryUtil.memAllocFloat(count * 4);
        FloatBuffer mvp = MemoryUtil.memAllocFloat(16); // 4x4 matrix
        FloatBuffer outVerts = MemoryUtil.memAllocFloat(count * 4);

        for (int i = 0; i < count * 4; i++) verts.put(i, 1.0f);
        for (int i = 0; i < 16; i++) mvp.put(i, 1.0f);

        int shader = GL43.glCreateShader(GL43.GL_COMPUTE_SHADER);
        GL43.glShaderSource(shader, ShaderLoader.load("assets/yttrium/shaders/RenderTransform.glsl"));
        GL43.glCompileShader(shader);

        int program = GL43.glCreateProgram();
        GL43.glAttachShader(program, shader);
        GL43.glLinkProgram(program);
        GL43.glUseProgram(program);

        int bufV = GL43.glGenBuffers();
        int bufM = GL43.glGenBuffers();
        int bufR = GL43.glGenBuffers();

        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, bufV);
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, verts, GL43.GL_STATIC_DRAW);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, bufV);

        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, bufM);
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, mvp, GL43.GL_STATIC_DRAW);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, bufM);

        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, bufR);
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, outVerts, GL43.GL_STATIC_DRAW);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 2, bufR);

        long start = System.nanoTime();
        GL43.glDispatchCompute(count / 64, 1, 1);
        GL43.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
        long end = System.nanoTime();

        YttriumMod.LOGGER.info("Render transform GPU complete. Time: {} ms", (end - start) / 1_000_000.0);


        MemoryUtil.memFree(verts);
        MemoryUtil.memFree(mvp);
        MemoryUtil.memFree(outVerts);
    }
}
