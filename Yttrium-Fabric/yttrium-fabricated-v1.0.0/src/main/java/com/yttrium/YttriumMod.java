package com.yttrium;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class YttriumMod implements ModInitializer {
    public static final String MOD_ID = "yttrium";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Yttrium initializing — launching async GPU acceleration...");
        CompletableFuture.runAsync(() -> {
            try {
                if (MatrixGPU.isSupported()) {
                    MatrixGPU.runAll();
                } else {
                    LOGGER.warn("OpenGL 4.3 not supported — falling back to CPU.");
                    MatrixGPU.runFallback();
                }
            } catch (Exception e) {
                LOGGER.error("Yttrium GPU execution failed", e);
            }
        });
    }
}
