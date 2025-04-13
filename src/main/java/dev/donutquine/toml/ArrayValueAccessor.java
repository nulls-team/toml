package dev.donutquine.toml;

@SuppressWarnings("unused")
public interface ArrayValueAccessor {
    String getString(int index);

    int getInteger(int index);

    long getLong(int index);

    float getFloat(int index);

    boolean getBoolean(int index);

    // TODO:
    // Offset Date-Time
    // Local Date-Time
    // Local Date
    // Local Time

    TomlArray getArray(int index);

    TomlTable getTable(int index);
    
    <T> T getAs(int index, Class<T> type);

    int getSize();

    boolean isEmpty();
}
