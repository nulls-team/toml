package dev.donutquine.toml;

import dev.donutquine.toml.util.StringEscaper;

import java.util.HashMap;
import java.util.Iterator;
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
    public <T> T getAs(String key, Class<T> type) {
        Object value = values.get(key);
        if (type.isInstance(value)) {
            //noinspection unchecked
            return (T) value;
        }

        return null;
    }

    @Override
    public <T> T computeIfAbsent(String key, Function<String, T> valueFunction) {
        //noinspection unchecked
        return (T) values.computeIfAbsent(key, valueFunction);
    }

    @Override
    public void setString(String key, String value) {
        values.put(key, value);
    }

    @Override
    public void setInteger(String key, int value) {
        values.put(key, value);
    }

    @Override
    public void setLong(String key, long value) {
        values.put(key, value);
    }

    @Override
    public void setFloat(String key, float value) {
        values.put(key, value);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        values.put(key, value);
    }

    @Override
    public void setArray(String key, TomlArray value) {
        values.put(key, value);
    }

    @Override
    public void setTable(String key, TomlTable value) {
        values.put(key, value);
    }

    @Override
    public void setObject(String key, Object value) {
        values.put(key, value);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{");

        for (Iterator<Map.Entry<String, Object>> iterator = values.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
            Object value = entry.getValue();
            String valueString = value.toString();
            if (value instanceof String) {
                valueString = '"' + StringEscaper.escape(valueString) + '"';
            }

            result.append(entry.getKey()).append("=").append(valueString);

            if (iterator.hasNext()) {
                result.append(',');
            }
        }

        result.append("}");
        return result.toString();
    }
}
