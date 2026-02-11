
package dev.donutquine.toml;

import dev.donutquine.toml.util.ResourceLoader;
import org.junit.jupiter.api.Test;

import static dev.donutquine.toml.util.Assertions.assertArrayValueEquals;
import static org.junit.jupiter.api.Assertions.*;

public class TrailingCommaTest {
    @Test
    public void test() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/own/trailing_comma.toml"));
        Toml toml = assertDoesNotThrow(parser::parse);

        assertEquals(1, toml.getRootTable().getArray("trailing").getSize());
        assertArrayValueEquals(toml.getRootTable().getArray("trailing"), new String[]{"r1"}, String.class);

        assertTrue(toml.getRootTable().getBoolean("next_field"));
        assertFalse(toml.getRootTable().getTable("header").getBoolean("new_field"));
    }
}

