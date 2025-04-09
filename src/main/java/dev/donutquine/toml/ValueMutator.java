package dev.donutquine.toml;

public interface ValueMutator {
    void setString(String key, String value);

    void setInteger(String key, int value);

    void setLong(String key, long value);

    void setFloat(String key, float value);

    void setBoolean(String key, boolean value);

    // TODO:
    // Offset Date-Time
    // Local Date-Time
    // Local Date
    // Local Time

    void setArray(String key, TomlArray value);

    void setTable(String key, TomlTable value);

    void setObject(String key, Object value);
}
