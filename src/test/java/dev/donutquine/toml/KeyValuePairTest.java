package dev.donutquine.toml;

import dev.donutquine.toml.util.ResourceLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KeyValuePairTest {
    private static Toml toml;

    @BeforeAll
    static void setUpBeforeClass() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/spec/key_value.toml"));
        toml = assertDoesNotThrow(parser::parse);
    }

    @Test
    public void test() {
        assertTrue(toml.getRootTable().has("key"));
        assertEquals("value", toml.getRootTable().getString("key"));
    }
}
