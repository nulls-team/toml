package dev.donutquine.toml;

public interface ArrayValueMutator {
    void addString(String value);

    void addInteger(int value);

    void addLong(long value);

    void addFloat(float value);

    void addBoolean(boolean value);

    // TODO:
    // Offset Date-Time
    // Local Date-Time
    // Local Date
    // Local Time

    void addArray(TomlArray value);

    void addTable(TomlTable value);
}
