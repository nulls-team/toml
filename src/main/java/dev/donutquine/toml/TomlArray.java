package dev.donutquine.toml;

import dev.donutquine.toml.util.StringEscaper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO: maybe usage of generic ValueAccessor will be better (like ValueAccessor<Integer>)
public class TomlArray implements ArrayValueAccessor, ArrayValueMutator {
    private final List<Object> values = new ArrayList<>();

    @Override
    public String getString(int index) {
        return getAs(index, String.class);
    }

    @Override
    public int getInteger(int index) {
        return getAs(index, Integer.class);
    }

    @Override
    public long getLong(int index) {
        return getAs(index, Long.class);
    }

    @Override
    public float getFloat(int index) {
        return getAs(index, Float.class);
    }

    @Override
    public boolean getBoolean(int index) {
        return getAs(index, Boolean.class);
    }

    @Override
    public TomlArray getArray(int index) {
        return getAs(index, TomlArray.class);
    }

    @Override
    public TomlTable getTable(int index) {
        return getAs(index, TomlTable.class);
    }

    @Override
    public <T> T getAs(int index, Class<T> type) {
        Objects.checkIndex(index, values.size());

        Object value = values.get(index);
        if (type.isInstance(value)) {
            //noinspection unchecked
            return (T) value;
        }

        throw new IllegalArgumentException(value.getClass().getSimpleName() + " cannot be casted to " + type.getSimpleName());
    }

    @Override
    public int getSize() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
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
