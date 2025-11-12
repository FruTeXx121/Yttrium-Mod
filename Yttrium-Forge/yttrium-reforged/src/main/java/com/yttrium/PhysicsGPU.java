package com.yttrium;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

import java.nio.FloatBuffer;

public class PhysicsGPU {

    private static int program;
    private static final Logger LOGGER = YttriumMod.LOGGER;

    // Compile and link the compute shader
    public static void init() {
        try {
            ResourceManager rm = Minecraft.getInstance().getResourceManager();
            String source = ShaderLoader.load(rm, "PhysicsSim.glsl"); // adjust filename if needed

            int shader = GL43.glCreateShader(GL43.GL_COMPUTE_SHADER);
            GL43.glShaderSource(shader, source);
            GL43.glCompileShader(shader);

            program = GL43.glCreateProgram();
            GL43.glAttachShader(program, shader);
            GL43.glLinkProgram(program);

            LOGGER.info("PhysicsGPU shader compiled and linked.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize PhysicsGPU", e);
        }
    }

    // Run the compute shader
    public static void run() {
        int count = 64;
        FloatBuffer pos = MemoryUtil.memAllocFloat(count * 3);
        FloatBuffer vel = MemoryUtil.memAllocFloat(count * 3);
        FloatBuffer newPos = MemoryUtil.memAllocFloat(count * 3);

        for (int i = 0; i < count * 3; i++) {
            pos.put(i, 0.0f);
            vel.put(i, 0.1f);
        }

        GL43.glUseProgram(program);

        int bufP = GL43.glGenBuffers();
        int bufV = GL43.glGenBuffers();
        int bufR = GL43.glGenBuffers();

        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, bufP);
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, pos, GL43.GL_STATIC_DRAW);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, bufP);

        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, bufV);
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, vel, GL43.GL_STATIC_DRAW);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, bufV);

        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, bufR);
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, newPos, GL43.GL_STATIC_DRAW);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 2, bufR);

        int deltaTimeLoc = GL43.glGetUniformLocation(program, "deltaTime");
        GL43.glUniform1f(deltaTimeLoc, 0.016f); // ~60 FPS

        long start = System.nanoTime();
        GL43.glDispatchCompute(count / 64, 1, 1);
        GL43.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
        long end = System.nanoTime();

        LOGGER.info("PhysicsGPU complete. Time: {} ms", (end - start) / 1_000_000.0);

        MemoryUtil.memFree(pos);
        MemoryUtil.memFree(vel);
        MemoryUtil.memFree(newPos);
    }
}
