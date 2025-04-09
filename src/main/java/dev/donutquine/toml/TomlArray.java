package dev.donutquine.toml;

import java.util.ArrayList;
import java.util.List;

// TODO: maybe usage of generic ValueAccessor will be better (like ValueAccessor<Integer>)
public class TomlArray implements ArrayValueAccessor {
    private final List<Object> values = new ArrayList<>();

    @Override
    public String getString(int key) {
        return (String) values.get(key);
    }

    @Override
    public int getInteger(int key) {
        return (int) values.get(key);
    }

    @Override
    public long getLong(int key) {
        return (long) values.get(key);
    }

    @Override
    public float getFloat(int key) {
        return (float) values.get(key);
    }

    @Override
    public boolean getBoolean(int key) {
        return (boolean) values.get(key);
    }

    @Override
    public TomlArray getArray(int key) {
        return (TomlArray) values.get(key);
    }

    @Override
    public TomlTable getTable(int key) {
        return (TomlTable) values.get(key);
    }
}
