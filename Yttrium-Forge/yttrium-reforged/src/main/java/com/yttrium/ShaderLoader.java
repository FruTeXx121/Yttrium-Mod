package com.yttrium;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ShaderLoader {

    public static String load(ResourceManager resourceManager, String shaderName) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(
                YttriumMod.MODID, "shaders/" + shaderName
        );
        try {
            Resource resource = resourceManager.getResource(location).orElseThrow(
                    () -> new IOException("Shader file not found: " + location)
            );
            try (InputStream stream = resource.open()) {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shader: " + shaderName, e);
        }
    }
}
