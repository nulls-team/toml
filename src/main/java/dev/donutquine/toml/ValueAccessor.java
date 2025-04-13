package dev.donutquine.toml;

import java.util.List;

public interface ValueAccessor {
    String getString(String key);

    int getInteger(String key);

    long getLong(String key);

    float getFloat(String key);

    boolean getBoolean(String key);

    // TODO:
    // Offset Date-Time
    // Local Date-Time
    // Local Date
    // Local Time

    TomlArray getArray(String key);

    TomlTable getTable(String key);

    <T> T getAs(String key, Class<T> type);

    List<String> getKeys();

    boolean has(String key);

    boolean isEmpty();
}
