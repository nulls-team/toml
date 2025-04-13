package dev.donutquine.toml;

import java.util.List;

@SuppressWarnings("unused")
public interface ValueAccessor {
    String getString(String key);

    int getInteger(String key);

    int getInteger(String key, int defaultValue);

    long getLong(String key);

    long getLong(String key, long defaultValue);

    float getFloat(String key);

    float getFloat(String key, float defaultValue);

    boolean getBoolean(String key);

    boolean getBoolean(String key, boolean defaultValue);

    // TODO:
    // Offset Date-Time
    // Local Date-Time
    // Local Date
    // Local Time

    TomlArray getArray(String key);

    TomlTable getTable(String key);

    <T> T getAs(String key, Class<T> type);

    <T> T getAs(String key, T defaultValue, Class<T> type);

    List<String> getKeys();

    boolean has(String key);

    boolean isEmpty();
}
