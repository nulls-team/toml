package dev.donutquine.toml;

import java.util.function.Function;

public interface TomlTable extends ValueAccessor {
    <T> T computeIfAbsent(String key, Function<String, T> valueFunction);
}
