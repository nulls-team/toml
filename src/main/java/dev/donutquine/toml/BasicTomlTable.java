package dev.donutquine.toml;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// TODO: maybe add InlineTomlTable
public class BasicTomlTable implements TomlTable {
    // TODO: Maybe use something like TomlValue<?>?
    private final Map<String, Object> values = new HashMap<>();

    @Override
    public String getString(String key) {
        return (String) values.get(key);
    }

    @Override
    public int getInteger(String key) {
        return (int) values.get(key);
    }

    @Override
    public long getLong(String key) {
        return (long) values.get(key);
    }

    @Override
    public float getFloat(String key) {
        return (float) values.get(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return (boolean) values.get(key);
    }

    @Override
    public TomlArray getArray(String key) {
        return (TomlArray) values.get(key);
    }

    @Override
    public TomlTable getTable(String key) {
        return (TomlTable) values.get(key);
    }

    @Override
    public <T> T computeIfAbsent(String key, Function<String, T> valueFunction) {
        //noinspection unchecked
        return (T) values.computeIfAbsent(key, valueFunction);
    }
}
