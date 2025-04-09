package dev.donutquine.toml;

public interface ArrayValueAccessor {
    String getString(int key);

    int getInteger(int key);

    long getLong(int key);

    float getFloat(int key);

    boolean getBoolean(int key);

    // TODO:
    // Offset Date-Time
    // Local Date-Time
    // Local Date
    // Local Time

    TomlArray getArray(int key);

    TomlTable getTable(int key);
}
