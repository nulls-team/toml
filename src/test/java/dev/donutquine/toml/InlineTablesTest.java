
package dev.donutquine.toml;

import dev.donutquine.toml.util.ResourceLoader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InlineTablesTest {
    @Test
    public void testInlineTablesInArray() {
        TomlParser parser = new TomlParser(ResourceLoader.getStringResource("/own/inline_tables_in_array.toml"));
        Toml toml = assertDoesNotThrow(parser::parse);

        TomlArray dependencies = toml.getRootTable().getArray("dependencies");
        assertNotNull(dependencies);

        assertEquals(3, dependencies.getSize());

        TomlTable inlineTable0 = dependencies.getTable(0);
        assertEquals(2, inlineTable0.getKeys().size());
        assertEquals("remote", inlineTable0.getString("type"));
        assertEquals("example.com", inlineTable0.getString("url"));

        TomlTable inlineTable1 = dependencies.getTable(1);
        assertEquals(2, inlineTable1.getKeys().size());
        assertEquals("local", inlineTable1.getString("type"));
        assertEquals("./example/", inlineTable1.getString("path"));

        TomlTable inlineTable2 = dependencies.getTable(2);
        assertTrue(inlineTable2.getKeys().isEmpty());
    }
}

