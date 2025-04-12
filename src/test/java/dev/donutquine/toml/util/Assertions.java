package dev.donutquine.toml.util;

import dev.donutquine.toml.TomlTable;
import dev.donutquine.toml.ValueAccessor;

import static org.junit.jupiter.api.Assertions.*;

public class Assertions {
    private Assertions() {}

    public static <T> T assertValueEquals(ValueAccessor table, Object expected, String key) {
        assertTrue(table.has(key), table.toString());
        Object value = table.getAs(key, Object.class);
        assertEquals(expected, value, table.toString());

        //noinspection unchecked
        return (T) value;
    }

    public static ValueAccessor assertTableExists(ValueAccessor table, String key) {
        assertTrue(table.has(key));
        TomlTable result = table.getTable(key);
        assertNotNull(table);
        return result;
    }
}
