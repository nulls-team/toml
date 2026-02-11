package dev.donutquine.toml.util;

import dev.donutquine.toml.ArrayValueAccessor;
import dev.donutquine.toml.TomlTable;
import dev.donutquine.toml.ValueAccessor;

import java.lang.reflect.Array;

import static org.junit.jupiter.api.Assertions.*;

public class Assertions {
    private Assertions() {}

    public static String assertValueEquals(ValueAccessor table, String expected, String key) {
        assertTrue(table.has(key), table.toString());
        String value = table.getString(key);
        assertEquals(expected, value, table.toString());

        return value;
    }

    public static boolean assertValueEquals(ValueAccessor table, boolean expected, String key) {
        assertTrue(table.has(key), table.toString());
        boolean value = table.getBoolean(key);
        assertEquals(expected, value, table.toString());

        return value;
    }

    public static int assertValueEquals(ValueAccessor table, int expected, String key) {
        assertTrue(table.has(key), table.toString());
        int value = table.getInteger(key);
        assertEquals(expected, value, table.toString());

        return value;
    }

    public static long assertValueEquals(ValueAccessor table, long expected, String key) {
        assertTrue(table.has(key), table.toString());
        long value = table.getLong(key);
        assertEquals(expected, value, table.toString());

        return value;
    }

    public static float assertValueEquals(ValueAccessor table, float expected, String key) {
        assertTrue(table.has(key), table.toString());
        long value = table.getLong(key);
        assertEquals(expected, value, table.toString());

        return value;
    }

    public static <T> T assertValueEquals(ValueAccessor table, T expected, String key) {
        assertTrue(table.has(key), table.toString());
        Object value = table.getAs(key, expected.getClass());
        assertEquals(expected, value, table.toString());

        //noinspection unchecked
        return (T) value;
    }

    public static <T> T[] assertArrayValueEquals(ArrayValueAccessor accessor, T[] expected, Class<T> type) {
        assertEquals(expected.length, accessor.getSize());

        @SuppressWarnings("unchecked") T[] result = (T[]) Array.newInstance(type, accessor.getSize());
        for (int i = 0; i < accessor.getSize(); i++) {
            result[i] = accessor.getAs(i, type);
        }
        assertArrayEquals(result, expected);
        return result;
    }

    public static ValueAccessor assertTableExists(ValueAccessor table, String key) {
        assertTrue(table.has(key));
        TomlTable result = table.getTable(key);
        assertNotNull(table);
        return result;
    }
}
