package dev.donutquine.toml.util;

import dev.donutquine.toml.TomlTable;
import dev.donutquine.toml.ValueAccessor;

import static org.junit.jupiter.api.Assertions.*;

public class Assertions {
    private Assertions() {}

    public static void assertValueEquals(ValueAccessor table, Object expected, String key) {
        assertTrue(table.has(key), table.toString());
        assertEquals(expected, table.getAs(key, Object.class), table.toString());
    }

    public static ValueAccessor assertTableExists(ValueAccessor table, String key) {
        assertTrue(table.has(key));
        TomlTable result = table.getTable(key);
        assertNotNull(table);
        return result;
    }
}
