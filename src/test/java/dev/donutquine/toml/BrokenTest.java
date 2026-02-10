
package dev.donutquine.toml;

import dev.donutquine.toml.util.ResourceLoader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BrokenTest {
    @Test
    public void testBroken() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/own/broken.toml"));
        Toml toml = assertDoesNotThrow(parser::parse);
    }
}
