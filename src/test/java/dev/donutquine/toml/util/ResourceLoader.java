package dev.donutquine.toml.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ResourceLoader {
    private ResourceLoader() {}

    public static String getStringResource(String resourceName) {
        try (InputStream resourceAsStream = ResourceLoader.class.getResourceAsStream(resourceName)) {
            return new String(Objects.requireNonNull(resourceAsStream).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
