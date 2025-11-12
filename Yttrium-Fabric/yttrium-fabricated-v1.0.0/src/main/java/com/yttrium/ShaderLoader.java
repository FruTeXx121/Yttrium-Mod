package com.yttrium;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class ShaderLoader {
    public static String load(String path) {
        try (InputStream in = ShaderLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IOException("Shader file not found: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shader: " + path, e);
        }
    }
}
