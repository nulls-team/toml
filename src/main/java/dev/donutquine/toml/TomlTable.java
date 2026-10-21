package dev.donutquine.toml;

import dev.donutquine.toml.util.StringEscaper;

import java.util.*;

// TODO: maybe add InlineTomlTable
public class TomlTable implements ValueAccessor, ValueMutator {
    // TODO: Maybe use something like TomlValue<?>?
    private final Map<String, Object> values = new HashMap<>();
    private final List<String> keys = new ArrayList<>();

    @Override
    public String getString(String key) {
        return getAs(key, null, String.class);
    }

    @Override
    public int getInteger(String key) {
        return getInteger(key, 0);
    }

    @Override
    public int getInteger(String key, int defaultValue) {
        return getAsOrThrow(key, getAs(key, defaultValue, Integer.class), Integer.class);
    }

    @Override
    public long getLong(String key) {
        return getLong(key, 0L);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        Integer integer = getAs(key, null, Integer.class);
        if (integer == null) {
            return getAsOrThrow(key, getAs(key, defaultValue, Long.class), Long.class);
        }

        return integer;
    }

    @Override
    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return getAsOrThrow(key, getAs(key, defaultValue, Float.class), Float.class);
    }

    @Override
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return getAsOrThrow(key, getAs(key, defaultValue, Boolean.class), Boolean.class);
    }

    @Override
    public TomlArray getArray(String key) {
        return getAs(key, TomlArray.class);
    }

    @Override
    public TomlTable getTable(String key) {
        return getAs(key, TomlTable.class);
    }

    @Override
    public <T> T getAs(String key, Class<T> type) {
        return getAs(key, null, type);
    }

    @Override
    public <T> T getAs(String key, T defaultValue, Class<T> type) {
        if (!values.containsKey(key)) {
            return defaultValue;
        }

        Object value = values.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        }

        return null;
    }

    @Override
    public List<String> getKeys() {
        return Collections.unmodifiableList(keys);
    }

    @Override
    public boolean has(String key) {
        return values.containsKey(key);
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public void setString(String key, String value) {
        setObject(key, value);
    }

    @Override
    public void setInteger(String key, int value) {
        setObject(key, value);
    }

    @Override
    public void setLong(String key, long value) {
        setObject(key, value);
    }

    @Override
    public void setFloat(String key, float value) {
        setObject(key, value);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        setObject(key, value);
    }

    @Override
    public void setArray(String key, TomlArray value) {
        setObject(key, value);
    }

    @Override
    public void setTable(String key, TomlTable value) {
        setObject(key, value);
    }

    @Override
    public void setObject(String key, Object value) {
        if (!values.containsKey(key)) {
            keys.add(key);
        }

        values.put(key, value);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{");

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = values.get(key);

            String valueString = value.toString();
            if (value instanceof String) {
                valueString = '"' + StringEscaper.escape(valueString) + '"';
            }

            result.append(key).append("=").append(valueString);

            if (i < keys.size() - 1) {
                result.append(", ");
            }
        }

        result.append("}");
        return result.toString();
    }

    public TomlArray ensureArray(String key) {
        if (has(key)) {
            return getAsOrThrow(key, getArray(key), TomlArray.class);
        }

        TomlArray array = new TomlArray();
        setArray(key, array);
        return array;
    }

    public TomlTable ensureTable(String key) {
        if (has(key)) {
            return getAsOrThrow(key, getTable(key), TomlTable.class);
        }

        TomlTable table = new TomlTable();
        setTable(key, table);
        return table;
    }

    private static <T> T getAsOrThrow(String key, T value, Class<T> type) {
        if (value == null) {
            throw new IllegalArgumentException(StringEscaper.escape(key) + " value cannot be casted to " + type.getSimpleName());
        }

        return value;
    }
}
