package dev.donutquine.toml;

import dev.donutquine.toml.util.StringEscaper;

import java.util.ArrayList;
import java.util.List;

// TODO: maybe usage of generic ValueAccessor will be better (like ValueAccessor<Integer>)
public class TomlArray implements ArrayValueAccessor, ArrayValueMutator {
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

    @Override
    public void addString(String value) {
        values.add(value);
    }

    @Override
    public void addInteger(int value) {
        values.add(value);
    }

    @Override
    public void addLong(long value) {
        values.add(value);
    }

    @Override
    public void addFloat(float value) {
        values.add(value);
    }

    @Override
    public void addBoolean(boolean value) {
        values.add(value);
    }

    @Override
    public void addArray(TomlArray value) {
        values.add(value);
    }

    @Override
    public void addTable(TomlTable value) {
        values.add(value);
    }

    @Override
    public void addObject(Object value) {
        values.add(value);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[");

        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            String valueString = value.toString();
            if (value instanceof String) {
                valueString = '"' + StringEscaper.escape(valueString) + '"';
            }

            result.append(valueString);

            if (i < values.size() - 1) {
                result.append(',');
            }
        }

        result.append("]");
        return result.toString();
    }
}
