package com.yttrium;

import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;

public class MatrixCPU {
    public static void run() {
        int size = 16;
        FloatBuffer a = MemoryUtil.memAllocFloat(size * size);
        FloatBuffer b = MemoryUtil.memAllocFloat(size * size);
        FloatBuffer c = MemoryUtil.memAllocFloat(size * size);

        for (int i = 0; i < size * size; i++) {
            a.put(i, 1.0f);
            b.put(i, 2.0f);
        }

        long start = System.nanoTime();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                float sum = 0;
                for (int k = 0; k < size; k++) {
                    sum += a.get(row * size + k) * b.get(k * size + col);
                }
                c.put(row * size + col, sum);
            }
        }
        long end = System.nanoTime();

        YttriumMod.LOGGER.info("CPU matrix multiplication complete. C[0] = {}", c.get(0));
        YttriumMod.LOGGER.info("CPU time: {} ms", (end - start) / 1_000_000.0);

        MemoryUtil.memFree(a);
        MemoryUtil.memFree(b);
        MemoryUtil.memFree(c);
    }
}
